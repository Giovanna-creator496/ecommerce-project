package it.giovanna.ecommerce.report;

import it.giovanna.ecommerce.model.CreditCardPayment;
import it.giovanna.ecommerce.model.PayPalPayment;
import org.springframework.stereotype.Component;

@Component
public class CsvExportVisitor implements PaymentVisitor<String> {

    @Override
    public String visit(CreditCardPayment payment) {

        return String.join(",",
                "CREDIT_CARD",
                payment.getId().toString(),
                payment.getAmount().getAmount().toString(),
                payment.getAmount().getCurrencyCode(),
                payment.getStatus().name(),
                "****" + payment.getLastFourDigits()
        );
    }

    @Override
    public String visit(PayPalPayment payment) {

        return String.join(",",
                "PAYPAL",
                payment.getId().toString(),
                payment.getAmount().getAmount().toString(),
                payment.getAmount().getCurrencyCode(),
                payment.getStatus().name(),
                payment.getPayerEmail()
        );
    }
}
