package it.giovanna.ecommerce.checkout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderContext {

    private Long customerId;
    private List<OrderItemRequest> items;
    private BigDecimal calculatedTotal = BigDecimal.ZERO;

    public OrderContext(Long customerId, List<OrderItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
        this.calculatedTotal = BigDecimal.ZERO;
    }


    public record OrderItemRequest(Long productId, Integer quantity) {}
}
