package it.giovanna.ecommerce.undo;

import it.giovanna.ecommerce.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UndoPurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {
}