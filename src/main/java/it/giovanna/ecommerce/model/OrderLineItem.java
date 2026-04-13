package it.giovanna.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name="order_line_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of="id")
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
}
