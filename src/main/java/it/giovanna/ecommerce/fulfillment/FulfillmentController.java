package it.giovanna.ecommerce.fulfillment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class FulfillmentController {

    private final OrderService orderService;

    @PostMapping("/{id}/route")
    public ResponseEntity<Void> routeOrder(
            @PathVariable Long id,
            @Valid @RequestBody FulfillmentRequest request
    ) {

        if (!id.equals(request.orderId())) {
            throw new IllegalArgumentException(
                    "L'orderId della richiesta non corrisponde al path"
            );
        }

        orderService.routeOrder(request);

        return ResponseEntity.noContent().build();
    }
}
