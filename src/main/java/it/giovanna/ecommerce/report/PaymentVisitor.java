package it.giovanna.ecommerce.report;

import it.giovanna.ecommerce.model.CreditCardPayment;
import it.giovanna.ecommerce.model.PayPalPayment;

public interface PaymentVisitor<T> {

    T visit(CreditCardPayment creditCardPayment);

    T visit(PayPalPayment payPalPayment);
}
