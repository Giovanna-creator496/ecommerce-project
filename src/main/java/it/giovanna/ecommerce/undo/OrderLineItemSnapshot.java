package it.giovanna.ecommerce.undo;

import java.math.BigDecimal;

public record OrderLineItemSnapshot(
        Integer quantity,
        BigDecimal lockedPriceAmount,
        String lockedPriceCurrency
) {
}
