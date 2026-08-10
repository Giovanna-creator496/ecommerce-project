package it.giovanna.Ecommerce.repository;

import it.giovanna.ecommerce.EcommerceApplication;
import it.giovanna.ecommerce.model.*;
import it.giovanna.ecommerce.repository.*;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ContextConfiguration(classes= EcommerceApplication.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CrucibleIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired private TestEntityManager entityManager;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private PurchaseOrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderLineItemRepository lineItemRepository;

    private Money createTestMoney(String amountStr, String currency) {
        Money money = new Money();
        try {
            var amountField = Money.class.getDeclaredField("amount");
            amountField.setAccessible(true);
            amountField.set(money, new BigDecimal(amountStr));

            var currencyField = Money.class.getDeclaredField("currencyCode");
            currencyField.setAccessible(true);
            currencyField.set(money, currency);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'impostazione di Money tramite Reflection", e);
        }
        return money;
    }


    private Customer createTestCustomer(String customerNum, String email) {
        return Customer.builder()
                .customerNumber(customerNum)
                .email(email)
                .createdAt(Instant.now())
                .build();
    }


    private PurchaseOrder createTestOrder(String orderNumber) {
        return PurchaseOrder.builder()
                .orderNumber(orderNumber)
                .orderDate(OffsetDateTime.now())
                .status(Status.PENDING)
                .orderLineItems(new HashSet<>())
                .build();
    }

    private Product createTestProduct(String sku, String name) {
        return Product.builder()
                .sku(sku)
                .name(name)
                .price(createTestMoney("99.99", "EUR"))
                .prodStatus(ProductStatus.AVAILABLE)
                .build();
    }


    // 1. THE DIRTY CHECKING TEST
    @Test
    @DisplayName("1. Dirty Checking Test - JPA aggiorna il DB senza repository.save()")
    void dirtyCheckingTest() {
        Customer customer = customerRepository.saveAndFlush(createTestCustomer("CUST-001", "old.email@example.com"));
        UUID customerId = customer.getId();


        customer.setEmail("new.email@example.com");

        entityManager.flush();
        entityManager.clear();

        Customer updatedCustomer = customerRepository.findById(customerId).orElseThrow();
        assertThat(updatedCustomer.getEmail()).isEqualTo("new.email@example.com");
    }

    // 2. THE N+1 QUERY RESOLUTION TEST
    @Test
    @DisplayName("2. N+1 Query Test - Verifica caricamento ottimizzato degli ordini dal Seed Data")
    void nPlusOneQueryResolutionTest() {
        entityManager.clear();

        List<PurchaseOrder> orders = orderRepository.findAllWithOrderLineItemsBy();

        assertThat(orders).hasSize(6);

        for (PurchaseOrder order : orders) {
            assertThat(order.getOrderLineItems()).isNotNull();
        }
    }


    // 3. THE ORPHAN REMOVAL TEST
    @Test
    @DisplayName("3. Orphan Removal Test - Rimuovendo un'item dalla collezione viene eliminato dal DB")
    void orphanRemovalTest() {
        PurchaseOrder order = orderRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Ordine 1 non trovato nel DB!"));

        assertThat(order.getOrderLineItems()).isNotEmpty();

        OrderLineItem itemToRemove = order.getOrderLineItems().iterator().next();
        Long itemToRemoveId = itemToRemove.getId();

        order.getOrderLineItems().remove(itemToRemove);
        itemToRemove.setPurchaseOrder(null);

        orderRepository.saveAndFlush(order);
        entityManager.clear();

        boolean existsInDb = lineItemRepository.existsById(itemToRemoveId);
        assertThat(existsInDb).isFalse();
    }

    // 4. THE DB CONSTRAINT SAFETY NET TEST
    @Test
    @DisplayName("4. Constraint Safety Net Test - Salvando un ordine con numero duplicato viene sollevata un'eccezione di vincolo")
    void duplicateOrderNumberShouldThrowExceptionTest() {
        PurchaseOrder existingOrder = orderRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Ordine 1 non trovato!"));

        String duplicateOrderNumber = existingOrder.getOrderNumber();

        PurchaseOrder duplicateOrder = PurchaseOrder.builder()
                .orderNumber(duplicateOrderNumber) // <--- Numero duplicato!
                .orderDate(java.time.OffsetDateTime.now())
                .status(Status.PENDING) // O uno status valido del tuo enum
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> {
                    orderRepository.saveAndFlush(duplicateOrder);
                },
                "Ci si aspettava una DataIntegrityViolationException a causa del numero ordine duplicato!"
        );
    }
}