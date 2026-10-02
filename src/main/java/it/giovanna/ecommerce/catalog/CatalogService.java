package it.giovanna.ecommerce.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogProductRepository catalogProductRepository;
    private final PricingStrategyFactory pricingStrategyFactory;

    @Transactional(readOnly = true)
    public Page<ProductSummaryView> getProducts(Pageable pageable, String role) {

        Page<ProductSummaryView> projectionPage =
                catalogProductRepository.findProductSummaries(pageable);

        PricingStrategy pricingStrategy =
                pricingStrategyFactory.getStrategy(role);

        return projectionPage.map(view -> new ProductSummaryView() {

            @Override
            public Long getId() {
                return view.getId();
            }

            @Override
            public String getName() {
                return view.getName();
            }

            @Override
            public java.math.BigDecimal getPriceAmount() {
                return pricingStrategy.calculatePrice(view.getPriceAmount());
            }
        });
    }
}
