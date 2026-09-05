package it.giovanna.ecommerce.catalog;

import java.math.BigDecimal;

public class BlackFridayDecorator implements PricingStrategy {

    private static final BigDecimal BLACK_FRIDAY_DISCOUNT = new BigDecimal("0.80");

    private final PricingStrategy pricingStrategy;

    public BlackFridayDecorator(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return pricingStrategy
                .calculatePrice(basePrice)
                .multiply(BLACK_FRIDAY_DISCOUNT);
    }
}
