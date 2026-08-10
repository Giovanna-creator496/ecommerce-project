package it.giovanna.ecommerce.repository;
import it.giovanna.ecommerce.model.Address;
import it.giovanna.ecommerce.model.PurchaseOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long>{
    @EntityGraph(attributePaths={"customer"})
    List<PurchaseOrder> findAllWithCustomerBy();
}
