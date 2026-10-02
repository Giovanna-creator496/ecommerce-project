package it.giovanna.ecommerce.undo;

import it.giovanna.ecommerce.model.Status;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderSnapshot(
        String orderNumber,
        OffsetDateTime orderDate,
        Status status,
        List<OrderLineItemSnapshot> orderLineItems
) {
}