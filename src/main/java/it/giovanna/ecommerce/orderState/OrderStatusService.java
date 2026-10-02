package it.giovanna.ecommerce.orderState;

import it.giovanna.ecommerce.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void updateStatus(Long orderId, Status newStatus) {

        if (!orderStatusRepository.existsById(orderId)) {
            throw new OrderNotFoundException(
                    "Ordine non trovato con ID: " + orderId
            );
        }

        int updatedRows = orderStatusRepository.updateStatus(orderId, newStatus);

        if (updatedRows != 1) {
            throw new IllegalStateException(
                    "Impossibile aggiornare lo stato dell'ordine con ID: " + orderId
            );
        }

        if (newStatus == Status.SHIPPED) {
            eventPublisher.publishEvent(
                    new OrderShippedEvent(orderId)
            );
        }
    }
}
