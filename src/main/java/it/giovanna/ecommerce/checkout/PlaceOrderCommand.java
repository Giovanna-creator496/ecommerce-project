package it.giovanna.ecommerce.checkout;

import it.giovanna.ecommerce.model.Money;
import it.giovanna.ecommerce.model.OrderLineItem;
import it.giovanna.ecommerce.model.Product;
import it.giovanna.ecommerce.model.PurchaseOrder;
import it.giovanna.ecommerce.model.Status;
import it.giovanna.ecommerce.repository.ProductRepository;
import it.giovanna.ecommerce.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlaceOrderCommand {

    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public PurchaseOrder execute(OrderContext context) {

        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<OrderLineItem> lineItems = new HashSet<>();

        PurchaseOrder order = new PurchaseOrder();


        order.setOrderNumber(
                "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
        order.setOrderDate(OffsetDateTime.now());
        order.setStatus(Status.PENDING);

        for (OrderContext.OrderItemRequest itemReq : context.getItems()) {

            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() ->
                            new CheckoutValidationException(
                                    "Prodotto non trovato con ID: " + itemReq.productId()
                            )
                    );


            product.setStockQuantity(
                    product.getStockQuantity() - itemReq.quantity()
            );

            BigDecimal itemPrice = product.getPrice().getAmount();

            BigDecimal subtotal = itemPrice.multiply(
                    BigDecimal.valueOf(itemReq.quantity())
            );

            totalAmount = totalAmount.add(subtotal);

            OrderLineItem lineItem = new OrderLineItem();

            lineItem.setQuantity(itemReq.quantity());

            lineItem.setLockedPrice(
                    new Money(
                            itemPrice,
                            product.getPrice().getCurrencyCode()
                    )
            );

            lineItem.setPurchaseOrder(order);

            lineItems.add(lineItem);
        }

        order.setOrderLineItems(lineItems);

        context.setCalculatedTotal(totalAmount);

        return purchaseOrderRepository.save(order);
    }
}
