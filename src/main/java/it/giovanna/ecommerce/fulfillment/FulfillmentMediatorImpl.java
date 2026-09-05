package it.giovanna.ecommerce.fulfillment;

import it.giovanna.ecommerce.model.PurchaseOrder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FulfillmentMediatorImpl
        implements FulfillmentMediator {

    private final FulfillmentOrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final WarehouseService warehouseService;
    private final BillingService billingService;
    private final ShippingZoneFactory shippingZoneFactory;

    @Override
    @Transactional
    public void route(FulfillmentRequest request) {

        PurchaseOrder order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordine non trovato con ID: " + request.orderId()
                ));

        ShippingZone zone =
                shippingZoneFactory.getZone(request.zoneCode());

        inventoryService.deductInventory(order);

        warehouseService.routeToWarehouse(order, zone);

        billingService.notifyBilling(order, zone);
    }
}