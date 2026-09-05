package it.giovanna.ecommerce.orderState;

import it.giovanna.ecommerce.model.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderStatusService orderStatusService;

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "USER") String role,
            @Valid @RequestBody OrderStatusRequest request) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        orderStatusService.updateStatus(id, request.getStatus());

        return ResponseEntity.noContent().build();
    }
}
