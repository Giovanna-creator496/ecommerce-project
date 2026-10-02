package it.giovanna.ecommerce.undo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.giovanna.ecommerce.model.Money;
import it.giovanna.ecommerce.model.OrderLineItem;
import it.giovanna.ecommerce.model.PurchaseOrder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class OrderUndoService {

    private final UndoPurchaseOrderRepository purchaseOrderRepository;
    private final OrderDraftMementoRepository mementoRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void addLineItem(
            Long orderId,
            AddOrderLineItemRequest request
    ) {

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordine non trovato con ID: " + orderId
                ));

        saveSnapshot(order);

        if (order.getOrderLineItems() == null) {
            order.setOrderLineItems(new LinkedHashSet<>());
        }

        OrderLineItem item = OrderLineItem.builder()
                .quantity(request.quantity())
                .lockedPrice(new Money(
                        request.priceAmount(),
                        request.currencyCode()
                ))
                .purchaseOrder(order)
                .build();

        order.getOrderLineItems().add(item);

        purchaseOrderRepository.save(order);
    }

    @Transactional
    public void undo(Long orderId) {

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordine non trovato con ID: " + orderId
                ));

        OrderDraftMemento memento =
                mementoRepository
                        .findFirstByPurchaseOrderIdOrderBySavedAtDesc(orderId)
                        .orElseThrow(() -> new IllegalStateException(
                                "Nessun memento disponibile per l'ordine con ID: "
                                        + orderId
                        ));

        try {
            OrderSnapshot snapshot = objectMapper.readValue(
                    memento.getStateJson(),
                    OrderSnapshot.class
            );

            order.restore(snapshot);

            purchaseOrderRepository.save(order);

            mementoRepository.delete(memento);

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Impossibile deserializzare lo snapshot dell'ordine",
                    exception
            );
        }
    }

    private void saveSnapshot(PurchaseOrder order) {

        OrderSnapshot snapshot = order.createSnapshot();

        try {
            String stateJson =
                    objectMapper.writeValueAsString(snapshot);

            OrderDraftMemento memento = OrderDraftMemento.builder()
                    .purchaseOrderId(order.getId())
                    .stateJson(stateJson)
                    .savedAt(Instant.now())
                    .build();

            mementoRepository.save(memento);

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Impossibile serializzare lo snapshot dell'ordine",
                    exception
            );
        }
    }
}