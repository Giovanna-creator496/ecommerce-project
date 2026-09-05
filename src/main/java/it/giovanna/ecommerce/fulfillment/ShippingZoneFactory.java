package it.giovanna.ecommerce.fulfillment;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ShippingZoneFactory {

    private final ShippingZoneRepository shippingZoneRepository;

    private final Map<String, ShippingZone> zones =
            new ConcurrentHashMap<>();

    @PostConstruct
    public void loadZones() {

        shippingZoneRepository.findAll()
                .forEach(zone ->
                        zones.put(zone.getZoneCode(), zone)
                );
    }

    public ShippingZone getZone(String zoneCode) {

        ShippingZone zone = zones.get(zoneCode);

        if (zone == null) {
            throw new IllegalArgumentException(
                    "Shipping zone non trovata: " + zoneCode
            );
        }

        return zone;
    }
}
