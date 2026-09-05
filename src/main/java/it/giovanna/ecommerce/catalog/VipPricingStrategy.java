package it.giovanna.ecommerce.catalog;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VipPricingStrategy implements PricingStrategy {

    private static final BigDecimal VIP_DISCOUNT = new BigDecimal("0.90");

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(VIP_DISCOUNT);
    }
}
