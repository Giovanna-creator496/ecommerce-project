package it.giovanna.Ecommerce.service;

import it.giovanna.ecommerce.EcommerceApplication;
import it.giovanna.ecommerce.dto.ProductUpdateDTO;
import it.giovanna.ecommerce.model.Product;
import it.giovanna.ecommerce.model.ProductStatus;
import it.giovanna.ecommerce.repository.ProductRepository;
import it.giovanna.ecommerce.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = EcommerceApplication.class)
@ActiveProfiles("local")
@Transactional
class ProductOptimisticLockingTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @PersistenceContext
    private EntityManager entityManager;


    @Test
    @DisplayName("Dovrebbe aggiornare il prodotto quando la versione del DTO è corretta")
    void shouldUpdateProductWhenVersionIsCorrect() {

        Product product = productRepository.findBySku("TEST-001")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Prodotto TEST-001 non trovato!"
                        ));

        Long currentVersion = product.getVersion();

        String newName = product.getName().equals("Test Mouse")
                ? "Test Mouse Aggiornato"
                : "Test Mouse";

        ProductUpdateDTO dto = new ProductUpdateDTO();
        dto.setName(newName);
        dto.setProdStatus(product.getProdStatus());
        dto.setAmount(product.getPrice().getAmount());
        dto.setCurrencyCode(product.getPrice().getCurrencyCode());
        dto.setVersion(currentVersion);

        Product updated = productService.updateProduct(
                product.getId(),
                dto
        );

        assertThat(updated.getName())
                .isEqualTo(newName);

        assertThat(updated.getVersion())
                .isGreaterThan(currentVersion);
    }


    @Test
    @DisplayName("Dovrebbe sollevare ObjectOptimisticLockingFailureException in caso di modifiche concorrenti")
    void shouldThrowOptimisticLockingExceptionOnConcurrentUpdate() {

        String targetSku = "TEST-001";

        Product tx1Product = productRepository.findBySku(targetSku)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Prodotto TEST-001 non trovato!"
                        ));

        Long oldVersion = tx1Product.getVersion();

        entityManager.detach(tx1Product);


        Product tx2Product = productRepository.findBySku(targetSku)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Prodotto TEST-001 non trovato!"
                        ));

        assertThat(tx2Product.getVersion())
                .isEqualTo(oldVersion);

        tx2Product.setName("Modifica dalla Transazione B");

        productRepository.saveAndFlush(tx2Product);

        tx1Product.setProdStatus(ProductStatus.DISCONTINUED);


        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> productRepository.saveAndFlush(tx1Product),
                "Ci si aspettava una ObjectOptimisticLockingFailureException " +
                        "a causa del conflitto di versione!"
        );
    }
}
