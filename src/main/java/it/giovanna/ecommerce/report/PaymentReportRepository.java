package it.giovanna.ecommerce.report;

import it.giovanna.ecommerce.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentReportRepository
        extends JpaRepository<Payment, UUID> {
}
