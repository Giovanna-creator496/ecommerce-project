package it.giovanna.ecommerce.dto;

import it.giovanna.ecommerce.model.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateDTO {

    private String name;

    private ProductStatus prodStatus;

    private BigDecimal amount;

    private String currencyCode;

    private Long version;

}
