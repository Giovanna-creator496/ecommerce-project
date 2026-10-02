package it.giovanna.Ecommerce;

import it.giovanna.ecommerce.EcommerceApplication;
import it.giovanna.ecommerce.fulfillment.ShippingZone;
import it.giovanna.ecommerce.fulfillment.ShippingZoneFactory;
import it.giovanna.ecommerce.fulfillment.ShippingZoneRepository;
import it.giovanna.ecommerce.model.CreditCardPayment;
import it.giovanna.ecommerce.model.Money;
import it.giovanna.ecommerce.model.OrderLineItem;
import it.giovanna.ecommerce.model.Payment;
import it.giovanna.ecommerce.model.PaymentStatus;
import it.giovanna.ecommerce.model.PayPalPayment;
import it.giovanna.ecommerce.model.PurchaseOrder;
import it.giovanna.ecommerce.model.Status;
import it.giovanna.ecommerce.report.CsvExportVisitor;
import it.giovanna.ecommerce.report.PaymentReportRepository;
import it.giovanna.ecommerce.undo.OrderDraftMementoRepository;
import it.giovanna.ecommerce.undo.UndoPurchaseOrderRepository;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = EcommerceApplication.class,
        properties = "spring.jpa.properties.hibernate.generate_statistics=true"
)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AdvancedBehavioralPatternsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UndoPurchaseOrderRepository orderRepository;

    @Autowired
    private OrderDraftMementoRepository mementoRepository;

    @Autowired
    private PaymentReportRepository paymentReportRepository;

    @Autowired
    private CsvExportVisitor csvExportVisitor;

    @Autowired
    private ShippingZoneRepository shippingZoneRepository;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Test
    void shouldRestoreFirstOrderStateUsingMemento() throws Exception {

        /*
         * FIRST STATE
         */
        String orderNumber =
                "CRUCIBLE-MEMENTO-" + UUID.randomUUID();

        PurchaseOrder order = PurchaseOrder.builder()
                .orderNumber(orderNumber)
                .orderDate(OffsetDateTime.now())
                .status(Status.PENDING)
                .orderLineItems(new LinkedHashSet<>())
                .build();

        OrderLineItem firstItem = OrderLineItem.builder()
                .quantity(2)
                .lockedPrice(new Money(
                        new BigDecimal("50.00"),
                        "EUR"
                ))
                .purchaseOrder(order)
                .build();

        order.getOrderLineItems().add(firstItem);

        order = orderRepository.saveAndFlush(order);

        Long orderId = order.getId();

        /*
         * Verify the first state while the entity is managed.
         * Only scalar values leave the transaction.
         */
        OrderState initialState =
                transactionTemplate.execute(status -> {

                    PurchaseOrder savedOrder =
                            orderRepository.findById(orderId)
                                    .orElseThrow();

                    OrderLineItem item =
                            savedOrder.getOrderLineItems()
                                    .iterator()
                                    .next();

                    return new OrderState(
                            savedOrder.getStatus(),
                            savedOrder.getOrderLineItems().size(),
                            item.getQuantity(),
                            item.getLockedPrice().getAmount(),
                            item.getLockedPrice().getCurrencyCode()
                    );
                });

        assertNotNull(initialState);
        assertEquals(Status.PENDING, initialState.status());
        assertEquals(1, initialState.lineCount());
        assertEquals(2, initialState.quantity());
        assertEquals(
                new BigDecimal("50.00"),
                initialState.priceAmount()
        );
        assertEquals("EUR", initialState.currencyCode());

        /*
         * UPDATE
         *
         * The existing Slice 4 endpoint creates the Memento
         * and then adds the new line item.
         */
        mockMvc.perform(
                        post("/api/v1/orders/{id}/items", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "quantity": 5,
                                          "priceAmount": 99.00,
                                          "currencyCode": "EUR"
                                        }
                                        """)
                )
                .andExpect(status().isNoContent());

        OrderState updatedState =
                transactionTemplate.execute(status -> {

                    PurchaseOrder updatedOrder =
                            orderRepository.findById(orderId)
                                    .orElseThrow();

                    return new OrderState(
                            updatedOrder.getStatus(),
                            updatedOrder.getOrderLineItems().size(),
                            null,
                            null,
                            null
                    );
                });

        assertNotNull(updatedState);
        assertEquals(2, updatedState.lineCount());

        /*
         * The previous state must have been saved as a Memento.
         */
        assertTrue(
                mementoRepository
                        .findFirstByPurchaseOrderIdOrderBySavedAtDesc(orderId)
                        .isPresent()
        );

        /*
         * UNDO
         */
        mockMvc.perform(
                        post("/api/v1/orders/{id}/undo", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        /*
         * Verify the restored state inside a transaction.
         *
         * We return only immutable scalar values, never a detached
         * JPA entity or lazy collection.
         */
        OrderState restoredState =
                transactionTemplate.execute(status -> {

                    PurchaseOrder restoredOrder =
                            orderRepository.findById(orderId)
                                    .orElseThrow();

                    OrderLineItem restoredItem =
                            restoredOrder.getOrderLineItems()
                                    .iterator()
                                    .next();

                    return new OrderState(
                            restoredOrder.getStatus(),
                            restoredOrder.getOrderLineItems().size(),
                            restoredItem.getQuantity(),
                            restoredItem.getLockedPrice().getAmount(),
                            restoredItem.getLockedPrice().getCurrencyCode()
                    );
                });

        assertNotNull(restoredState);

        /*
         * The FIRST state must be restored,
         * not the updated state.
         */
        assertEquals(Status.PENDING, restoredState.status());
        assertEquals(1, restoredState.lineCount());
        assertEquals(2, restoredState.quantity());
        assertEquals(
                new BigDecimal("50.00"),
                restoredState.priceAmount()
        );
        assertEquals(
                "EUR",
                restoredState.currencyCode()
        );

        /*
         * Undo consumes the Memento.
         */
        assertTrue(
                mementoRepository
                        .findFirstByPurchaseOrderIdOrderBySavedAtDesc(orderId)
                        .isEmpty()
        );
    }


    @Test
    void shouldExportCreditCardAndPayPalUsingVisitor() {

        /*
         * Create one instance of each concrete Payment subtype.
         */
        CreditCardPayment creditCardPayment =
                CreditCardPayment.builder()
                        .amount(new Money(
                                new BigDecimal("100.00"),
                                "EUR"
                        ))
                        .status(PaymentStatus.COMPLETED)
                        .lastFourDigits("1234")
                        .expirationMonth("12")
                        .expirationYear("2028")
                        .build();

        String payerEmail =
                "payer-" + UUID.randomUUID() + "@example.com";

        PayPalPayment payPalPayment =
                PayPalPayment.builder()
                        .amount(new Money(
                                new BigDecimal("75.00"),
                                "EUR"
                        ))
                        .status(PaymentStatus.COMPLETED)
                        .payerEmail(payerEmail)
                        .build();

        List<Payment> savedPayments =
                paymentReportRepository.saveAll(
                        List.of(
                                creditCardPayment,
                                payPalPayment
                        )
                );

        paymentReportRepository.flush();

        /*
         * Use the IDs generated for this test.
         *
         * This keeps the test independent from any other
         * Payment already present in the database.
         */
        java.util.Set<UUID> testPaymentIds =
                savedPayments.stream()
                        .map(Payment::getId)
                        .collect(java.util.stream.Collectors.toSet());

        /*
         * Polymorphic retrieval from the database.
         */
        List<Payment> payments =
                paymentReportRepository.findAll();

        List<Payment> testPayments =
                payments.stream()
                        .filter(payment ->
                                testPaymentIds.contains(payment.getId()))
                        .toList();

        assertEquals(2, testPayments.size());

        /*
         * Visitor dispatch is performed through accept().
         *
         * Sorting only makes the assertion deterministic because
         * findAll() does not guarantee database ordering.
         */
        List<String> csvLines =
                testPayments.stream()
                        .map(payment ->
                                payment.accept(csvExportVisitor))
                        .sorted()
                        .toList();

        /*
         * Line 1: Credit Card
         * Line 2: PayPal
         */
        assertTrue(
                csvLines.get(0).startsWith("CREDIT_CARD,")
        );

        assertTrue(
                csvLines.get(1).startsWith("PAYPAL,")
        );

        /*
         * Credit-card number is masked.
         */
        assertTrue(
                csvLines.get(0).contains("****1234")
        );

        assertFalse(
                csvLines.get(0).contains(",1234")
        );

        /*
         * PayPal-specific information is exported.
         */
        assertTrue(
                csvLines.get(1).contains(payerEmail)
        );
    }


    @Test
    void shouldLoadShippingZoneOnceAndUseFlyweightFor500Requests() {

        /*
         * The repository is mocked because this test is specifically
         * verifying the interaction between the Flyweight factory
         * and its repository dependency.
         */
        ShippingZoneRepository repository =
                Mockito.mock(ShippingZoneRepository.class);

        ShippingZone zone =
                ShippingZone.builder()
                        .id(1L)
                        .zoneCode("CRUCIBLE-ZONE")
                        .baseCost(new BigDecimal("10.00"))
                        .deliveryDays(2)
                        .build();

        when(repository.findAll())
                .thenReturn(List.of(zone));

        /*
         * Real production Flyweight factory.
         */
        ShippingZoneFactory factory =
                new ShippingZoneFactory(repository);

        /*
         * This represents the @PostConstruct initialization
         * performed by the production bean.
         */
        factory.loadZones();

        /*
         * Exactly one database repository load during initialization.
         */
        verify(repository, times(1)).findAll();

        ShippingZone cachedZone =
                factory.getZone("CRUCIBLE-ZONE");

        assertNotNull(cachedZone);
        assertSame(zone, cachedZone);

        /*
         * Reset Hibernate statistics.
         *
         * From this point forward, only the 500 Flyweight accesses
         * are measured.
         */
        Statistics statistics =
                sessionFactory.getStatistics();

        statistics.clear();

        /*
         * 500 requests must be served entirely from memory.
         */
        for (int i = 0; i < 500; i++) {

            ShippingZone result =
                    factory.getZone("CRUCIBLE-ZONE");

            assertSame(cachedZone, result);
        }

        /*
         * No Hibernate query must be generated by the 500 accesses.
         */
        assertEquals(
                0,
                statistics.getQueryExecutionCount(),
                "Le 500 richieste al Flyweight non devono eseguire query Hibernate"
        );

        assertEquals(
                0,
                statistics.getPrepareStatementCount(),
                "Le 500 richieste al Flyweight non devono generare statement SQL"
        );

        /*
         * Most important Flyweight assertion:
         * the repository was accessed exactly once, during loading,
         * and never again during the 500 requests.
         */
        verify(repository, times(1)).findAll();
    }


    private record OrderState(
            Status status,
            int lineCount,
            Integer quantity,
            BigDecimal priceAmount,
            String currencyCode
    ) {
    }
}
