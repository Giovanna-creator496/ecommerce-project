package it.giovanna.ecommerce.fulfillment;

import it.giovanna.ecommerce.model.PurchaseOrder;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    public void notifyBilling(
            PurchaseOrder order,
            ShippingZone zone
    ) {

    }
}
