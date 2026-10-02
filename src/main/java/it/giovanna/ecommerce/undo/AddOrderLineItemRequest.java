package it.giovanna.ecommerce.undo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddOrderLineItemRequest(

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        @Positive
        BigDecimal priceAmount,

        @NotNull
        String currencyCode
) {
}
