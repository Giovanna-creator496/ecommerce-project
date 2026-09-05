package it.giovanna.ecommerce.orderState;

import it.giovanna.ecommerce.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusRequest {

    @NotNull(message = "Lo status è obbligatorio")
    private Status status;
}
