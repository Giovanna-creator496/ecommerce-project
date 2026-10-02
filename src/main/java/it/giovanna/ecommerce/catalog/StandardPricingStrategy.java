package it.giovanna.ecommerce.catalog;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice;
    }
}