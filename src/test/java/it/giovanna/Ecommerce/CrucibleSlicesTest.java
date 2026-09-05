package it.giovanna.Ecommerce;

import it.giovanna.ecommerce.EcommerceApplication;
import it.giovanna.ecommerce.model.Money;
import it.giovanna.ecommerce.model.Product;
import it.giovanna.ecommerce.model.ProductStatus;
import it.giovanna.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EcommerceApplication.class)
@AutoConfigureMockMvc
@Testcontainers
class CrucibleSlicesTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        Instant now = Instant.now();
        String testRunId = UUID.randomUUID().toString();

        List<Product> products = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            products.add(
                    Product.builder()
                            .sku("CRUCIBLE-" + testRunId + "-" + i)
                            .name("Crucible Product " + i)
                            .prodStatus(ProductStatus.AVAILABLE)
                            .price(new Money(
                                    new BigDecimal("20.00"),
                                    "EUR"
                            ))
                            .stockQuantity(100)
                            .createdAt(now)
                            .updatedAt(now)
                            .createdBy("crucible-test")
                            .updatedBy("crucible-test")
                            .version(0L)
                            .build()
            );
        }

        productRepository.saveAllAndFlush(products);
    }

    @Test
    void shouldReturnPaginatedProductsUsingProjection() throws Exception {
        mockMvc.perform(
                        get("/api/v1/products")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.page.totalPages").value(5));
    }

    @Test
    void shouldApplyVipPricingStrategy() throws Exception {
        mockMvc.perform(
                        get("/api/v1/products")
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Tier", "VIP")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].priceAmount").value(18.0));
    }
}
