package it.giovanna.ecommerce.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PricingStrategyFactory {

    private final StandardPricingStrategy standardPricingStrategy;
    private final VipPricingStrategy vipPricingStrategy;

    @Value("${catalog.black-friday.enabled:false}")
    private boolean blackFridayEnabled;

    public PricingStrategy getStrategy(String role) {

        PricingStrategy strategy;

        if ("VIP".equalsIgnoreCase(role)) {
            strategy = vipPricingStrategy;
        } else {
            strategy = standardPricingStrategy;
        }

        if (blackFridayEnabled) {
            strategy = new BlackFridayDecorator(strategy);
        }

        return strategy;
    }
}
