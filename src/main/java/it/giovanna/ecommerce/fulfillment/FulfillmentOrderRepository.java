package it.giovanna.ecommerce.fulfillment;

import it.giovanna.ecommerce.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

 public interface FulfillmentOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {
}
