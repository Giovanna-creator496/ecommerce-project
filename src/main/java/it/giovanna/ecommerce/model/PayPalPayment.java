package it.giovanna.ecommerce.model;

import it.giovanna.ecommerce.report.PaymentVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("PAYPAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PayPalPayment extends Payment {

    @Column(name = "payer_email")
    private String payerEmail;

    @Override
    public <T> T accept(PaymentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
