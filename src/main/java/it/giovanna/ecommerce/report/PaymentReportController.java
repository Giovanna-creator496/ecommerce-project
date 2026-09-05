package it.giovanna.ecommerce.report;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports/payments")
@RequiredArgsConstructor
public class PaymentReportController {

    private final PaymentReportService paymentReportService;

    @GetMapping(
            value = "/export",
            produces = "text/csv"
    )
    public ResponseEntity<String> exportPayments() {

        return ResponseEntity.ok(
                paymentReportService.exportPayments()
        );
    }
}
