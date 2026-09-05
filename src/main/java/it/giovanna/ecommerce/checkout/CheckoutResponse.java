package it.giovanna.ecommerce.checkout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {

    private Long orderId;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private OffsetDateTime orderDate;
}
