package it.giovanna.ecommerce.catalog;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(BigDecimal basePrice);
}
