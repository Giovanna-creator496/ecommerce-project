package it.giovanna.ecommerce.fulfillment;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_zone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone_code", nullable = false, unique = true)
    private String zoneCode;

    @Column(name = "base_cost", nullable = false, precision = 19, scale = 2)
    private BigDecimal baseCost;

    @Column(name = "delivery_days", nullable = false)
    private Integer deliveryDays;
}
