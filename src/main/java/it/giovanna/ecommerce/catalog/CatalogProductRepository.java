package it.giovanna.ecommerce.catalog;

import it.giovanna.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CatalogProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p.id AS id,
                   p.name AS name,
                   p.price.amount AS priceAmount
            FROM Product p
            """)
    Page<ProductSummaryView> findProductSummaries(Pageable pageable);

    List<Product> findAllByIdIn(List<Long> ids);
}
