package it.giovanna.ecommerce.checkout;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Il customerId è obbligatorio")
    private Long customerId;

    @NotEmpty(message = "L'ordine deve contenere almeno un articolo")
    @Valid
    private List<ItemRequest> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemRequest {

        @NotNull(message = "Il productId è obbligatorio")
        private Long productId;

        @NotNull(message = "La quantità è obbligatoria")
        @Positive(message = "La quantità deve essere maggiore di zero")
        private Integer quantity;
    }
}