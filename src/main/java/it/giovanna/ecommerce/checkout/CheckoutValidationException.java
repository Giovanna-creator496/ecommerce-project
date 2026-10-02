package it.giovanna.ecommerce.checkout;

public class CheckoutValidationException extends RuntimeException {
    public CheckoutValidationException(String message) {
        super(message);
    }
}
