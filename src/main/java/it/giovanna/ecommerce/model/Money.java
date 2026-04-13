package it.giovanna.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class Money {
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name="currency_code",nullable = false)
    private String currencyCode;
}
