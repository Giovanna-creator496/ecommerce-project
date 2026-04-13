package it.giovanna.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="shipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of="trackingNumber")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="tracking_number",nullable = false,unique = true)
    private String trackingNumber;

    @Column(name="shipped_at",nullable = false)
    private OffsetDateTime shippedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true, name="purchase_order_id")
    private PurchaseOrder purchaseOrder;
}
