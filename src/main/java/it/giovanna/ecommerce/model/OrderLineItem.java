package it.giovanna.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.Objects;

@Entity
@Table(name="order_line_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Embedded
    private Money lockedPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="purchase_order_id",nullable = false)
    private PurchaseOrder purchaseOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLineItem)) return false;
        OrderLineItem other = (OrderLineItem) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}