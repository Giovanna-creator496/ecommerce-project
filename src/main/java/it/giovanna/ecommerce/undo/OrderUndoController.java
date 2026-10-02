package it.giovanna.ecommerce.undo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderUndoController {

    private final OrderUndoService orderUndoService;

    @PostMapping("/{id}/items")
    public ResponseEntity<Void> addLineItem(
            @PathVariable Long id,
            @Valid @RequestBody AddOrderLineItemRequest request
    ) {

        orderUndoService.addLineItem(id, request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/undo")
    public ResponseEntity<Void> undo(
            @PathVariable Long id
    ) {

        orderUndoService.undo(id);

        return ResponseEntity.noContent().build();
    }
}
