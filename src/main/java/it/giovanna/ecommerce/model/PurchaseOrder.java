package it.giovanna.ecommerce.model;

import it.giovanna.ecommerce.undo.OrderLineItemSnapshot;
import it.giovanna.ecommerce.undo.OrderSnapshot;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = "orderNumber", callSuper = false)
public class PurchaseOrder extends AuditableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "order_date", nullable = false)
    private OffsetDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(
            mappedBy = "purchaseOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("id ASC")
    private Set<OrderLineItem> orderLineItems;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "payment_id",
            referencedColumnName = "id",
            unique = true
    )
    private Payment payment;

    public OrderSnapshot createSnapshot() {

        Set<OrderLineItem> items =
                orderLineItems != null
                        ? orderLineItems
                        : Set.of();

        return new OrderSnapshot(
                orderNumber,
                orderDate,
                status,
                items.stream()
                        .map(item -> new OrderLineItemSnapshot(
                                item.getQuantity(),
                                item.getLockedPrice().getAmount(),
                                item.getLockedPrice().getCurrencyCode()
                        ))
                        .toList()
        );
    }

    public void restore(OrderSnapshot snapshot) {

        this.orderNumber = snapshot.orderNumber();
        this.orderDate = snapshot.orderDate();
        this.status = snapshot.status();

        if (this.orderLineItems == null) {
            this.orderLineItems = new java.util.LinkedHashSet<>();
        } else {
            this.orderLineItems.clear();
        }

        for (OrderLineItemSnapshot itemSnapshot :
                snapshot.orderLineItems()) {

            OrderLineItem item = OrderLineItem.builder()
                    .quantity(itemSnapshot.quantity())
                    .lockedPrice(new Money(
                            itemSnapshot.lockedPriceAmount(),
                            itemSnapshot.lockedPriceCurrency()
                    ))
                    .purchaseOrder(this)
                    .build();

            this.orderLineItems.add(item);
        }
    }
}
