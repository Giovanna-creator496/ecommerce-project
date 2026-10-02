package it.giovanna.ecommerce.undo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDraftMementoRepository
        extends JpaRepository<OrderDraftMemento, Long> {

    Optional<OrderDraftMemento>
    findFirstByPurchaseOrderIdOrderBySavedAtDesc(Long purchaseOrderId);
}
