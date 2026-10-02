package it.giovanna.ecommerce.checkout;

import it.giovanna.ecommerce.model.PurchaseOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutFacade {

    private final List<CheckoutValidatorStep> validatorSteps;
    private final PlaceOrderCommand placeOrderCommand;

    @Transactional
    public PurchaseOrder processCheckout(OrderContext context) {
        for (CheckoutValidatorStep step : validatorSteps) {
            step.validate(context);
        }

        return placeOrderCommand.execute(context);
    }
}
