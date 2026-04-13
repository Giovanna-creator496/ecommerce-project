package it.giovanna.ecommerce.model;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class Money {
    private BigDecimal amount;
    private String currencyCode;
}
