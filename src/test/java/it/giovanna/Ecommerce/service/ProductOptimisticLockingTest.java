package it.giovanna.Ecommerce.service;

import it.giovanna.ecommerce.EcommerceApplication;
import it.giovanna.ecommerce.dto.ProductUpdateDTO;
import it.giovanna.ecommerce.model.Money;
import it.giovanna.ecommerce.model.Product;
import it.giovanna.ecommerce.model.ProductStatus;
import it.giovanna.ecommerce.repository.ProductRepository;
import it.giovanna.ecommerce.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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

    private Product createTestProduct() {
        Instant now = Instant.now();
        String sku = "OPT-TEST-" + UUID.randomUUID();

        Product product = Product.builder()
                .sku(sku)
                .name("Test Product")
                .prodStatus(ProductStatus.AVAILABLE)
                .price(new Money(
                        new BigDecimal("99.99"),
                        "EUR"
                ))
                .stockQuantity(10)
                .createdAt(now)
                .updatedAt(now)
                .createdBy("test")
                .updatedBy("test")
                .build();

        return productRepository.saveAndFlush(product);
    }

    @Test
    @DisplayName("Dovrebbe aggiornare il prodotto quando la versione del DTO è corretta")
    void shouldUpdateProductWhenVersionIsCorrect() {

        Product product = createTestProduct();

        Long currentVersion = product.getVersion();
        String newName = "Test Product Aggiornato";

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

        Product product = createTestProduct();

        Long oldVersion = product.getVersion();
        Long productId = product.getId();

        entityManager.detach(product);

        Product tx2Product = productRepository.findById(productId)
                .orElseThrow();

        assertThat(tx2Product.getVersion())
                .isEqualTo(oldVersion);

        tx2Product.setName("Modifica dalla Transazione B");

        productRepository.saveAndFlush(tx2Product);

        product.setProdStatus(ProductStatus.DISCONTINUED);

        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> productRepository.saveAndFlush(product),
                "Ci si aspettava una ObjectOptimisticLockingFailureException " +
                        "a causa del conflitto di versione!"
        );
    }
}
