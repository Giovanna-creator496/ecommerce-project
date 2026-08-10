package it.giovanna.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Money {
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name="currency_code",nullable = false)
    private String currencyCode;
}
