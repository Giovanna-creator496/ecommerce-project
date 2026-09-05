package it.giovanna.ecommerce.catalog;

import java.math.BigDecimal;

public interface ProductSummaryView {

    Long getId();

    String getName();

    BigDecimal getPriceAmount();

}
