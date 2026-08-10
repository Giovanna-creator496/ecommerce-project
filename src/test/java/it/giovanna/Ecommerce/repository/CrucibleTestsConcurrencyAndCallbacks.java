package it.giovanna.Ecommerce.repository;

import it.giovanna.ecommerce.EcommerceApplication;
import it.giovanna.ecommerce.model.*;
import it.giovanna.ecommerce.repository.PaymentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = EcommerceApplication.class)
@ActiveProfiles("local")
@Transactional
class CrucibleTestsConcurrencyAndCallbacks {

    @Autowired
    private PaymentRepository paymentRepository;

    @PersistenceContext
    private EntityManager entityManager;


    // 2. AUTOMATED AUDITING TEST (Parte 5 - Punto 2)
    @Test
    @DisplayName("2. Automated Auditing Test: Verifica popolamento automatico dei campi di audit e incremento versione")
    void testAutomatedAuditing_shouldPopulateAndIncrementAuditFields() {
        CreditCardPayment payment = CreditCardPayment.builder()
                .amount(new Money(new BigDecimal("99.99"), "EUR"))
                .status(PaymentStatus.PENDING)
                .lastFourDigits("4321")
                .expirationMonth("12")
                .expirationYear("2028")
                .build();

        CreditCardPayment savedPayment = paymentRepository.saveAndFlush(payment);

        assertThat(savedPayment.getCreatedAt()).isNotNull();
        assertThat(savedPayment.getCreatedBy()).isNotNull();
        assertThat(savedPayment.getVersion()).isEqualTo(0L);


        savedPayment.setStatus(PaymentStatus.COMPLETED);
        CreditCardPayment updatedPayment = paymentRepository.saveAndFlush(savedPayment);

        assertThat(updatedPayment.getUpdatedAt()).isNotNull();
        assertThat(updatedPayment.getUpdatedAt()).isAfterOrEqualTo(updatedPayment.getCreatedAt());
        assertThat(updatedPayment.getVersion()).isEqualTo(1L);
    }

    // 3. POLYMORPHIC QUERY TEST (Parte 5 - Punto 3)
    @Test
    @DisplayName("3. Polymorphic Query Test: Verifica mapping automatico delle sottoclassi da payment_type")
    void testPolymorphicQuery_shouldRetrieveCorrectSubclasses() {
        CreditCardPayment creditCard = CreditCardPayment.builder()
                .amount(new Money(new BigDecimal("50.00"), "EUR"))
                .status(PaymentStatus.COMPLETED)
                .lastFourDigits("1111")
                .expirationMonth("05")
                .expirationYear("2029")
                .build();

        PayPalPayment payPal = PayPalPayment.builder()
                .amount(new Money(new BigDecimal("150.00"), "EUR"))
                .status(PaymentStatus.COMPLETED)
                .payerEmail("giovanna.test@example.com")
                .build();

        paymentRepository.saveAllAndFlush(List.of(creditCard, payPal));

        entityManager.clear();

        List<Payment> payments = paymentRepository.findAll();

        assertThat(payments).hasSizeGreaterThanOrEqualTo(2);

        boolean hasCreditCard = payments.stream().anyMatch(p -> p instanceof CreditCardPayment);
        boolean hasPayPal = payments.stream().anyMatch(p -> p instanceof PayPalPayment);

        assertThat(hasCreditCard).isTrue();
        assertThat(hasPayPal).isTrue();
    }
}
