package it.giovanna.ecommerce.checkout;

import it.giovanna.ecommerce.model.PurchaseOrder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutFacade checkoutFacade;

    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {
        try {
            List<OrderContext.OrderItemRequest> domainItems = request.getItems().stream()
                    .map(item -> new OrderContext.OrderItemRequest(item.getProductId(), item.getQuantity()))
                    .toList();

            OrderContext context = new OrderContext(request.getCustomerId(), domainItems);

            PurchaseOrder order = checkoutFacade.processCheckout(context);


            CheckoutResponse response = new CheckoutResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getStatus().name(),
                    context.getCalculatedTotal(),
                    order.getOrderDate()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (CheckoutValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
