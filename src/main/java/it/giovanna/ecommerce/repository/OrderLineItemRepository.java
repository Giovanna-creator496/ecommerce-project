package it.giovanna.ecommerce.repository;
import it.giovanna.ecommerce.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long>{
}
