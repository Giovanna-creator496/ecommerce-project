package it.giovanna.ecommerce.fulfillment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingZoneRepository
        extends JpaRepository<ShippingZone, Long> {
}
