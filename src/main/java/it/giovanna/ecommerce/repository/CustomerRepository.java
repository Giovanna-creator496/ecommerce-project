package it.giovanna.ecommerce.repository;
import it.giovanna.ecommerce.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer,UUID>{
}
