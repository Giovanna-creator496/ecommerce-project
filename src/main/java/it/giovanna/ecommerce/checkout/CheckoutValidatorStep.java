package it.giovanna.ecommerce.checkout;

public interface CheckoutValidatorStep {
    void validate(OrderContext context);
}
