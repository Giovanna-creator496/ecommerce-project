package it.giovanna.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name="purchase_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of="orderNumber", callSuper = false)
public class PurchaseOrder extends AuditableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_number",nullable = false, unique=true)
    private String orderNumber;

    @Column(name="order_date",nullable = false)
    private OffsetDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "purchaseOrder",orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<OrderLineItem> orderLineItems;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id", unique = true)
    private Payment payment;

}
