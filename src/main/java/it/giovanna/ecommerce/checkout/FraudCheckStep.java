
package it.giovanna.ecommerce.checkout;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FraudCheckStep implements CheckoutValidatorStep {

    @Override
    public void validate(OrderContext context) {
        int totalQuantity = context.getItems().stream()
                .mapToInt(OrderContext.OrderItemRequest::quantity)
                .sum();

        if (totalQuantity > 100) {
            throw new CheckoutValidationException("Ordine bloccato per controllo frodi: quantità pezzi eccessiva (" + totalQuantity + ").");
        }
    }
}