package it.giovanna.ecommerce.fulfillment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FulfillmentRequest(

        @NotNull
        Long orderId,

        @NotBlank
        String zoneCode
) {
}
