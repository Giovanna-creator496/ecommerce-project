package it.giovanna.ecommerce.orderState;

import it.giovanna.ecommerce.model.PurchaseOrder;
import it.giovanna.ecommerce.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderStatusRepository extends JpaRepository<PurchaseOrder, Long> {

    @Modifying
    @Query("""
            UPDATE PurchaseOrder p
            SET p.status = :status
            WHERE p.id = :id
            """)
    int updateStatus(
            @Param("id") Long id,
            @Param("status") Status status
    );
}
