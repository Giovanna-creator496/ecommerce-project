package it.giovanna.ecommerce.report;

import it.giovanna.ecommerce.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentReportService {

    private final PaymentReportRepository paymentReportRepository;
    private final CsvExportVisitor csvExportVisitor;

    @Transactional(readOnly = true)
    public String exportPayments() {

        List<Payment> payments = paymentReportRepository.findAll();

        StringBuilder csv = new StringBuilder();

        csv.append("payment_type,id,amount,currency,status,details\n");

        for (Payment payment : payments) {
            csv.append(payment.accept(csvExportVisitor))
                    .append('\n');
        }

        return csv.toString();
    }
}