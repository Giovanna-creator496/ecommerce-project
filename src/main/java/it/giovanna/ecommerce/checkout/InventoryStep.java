package it.giovanna.ecommerce.checkout;

import it.giovanna.ecommerce.model.Product;
import it.giovanna.ecommerce.model.ProductStatus;
import it.giovanna.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryStep implements CheckoutValidatorStep {

    private final ProductRepository productRepository;

    @Override
    public void validate(OrderContext context) {
        for (OrderContext.OrderItemRequest item : context.getItems()) {

            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() ->
                            new CheckoutValidationException(
                                    "Prodotto con ID " + item.productId() + " non trovato."
                            )
                    );

            if (product.getProdStatus() != ProductStatus.AVAILABLE) {
                throw new CheckoutValidationException(
                        "Il prodotto '" + product.getName()
                                + "' non è disponibile per l'acquisto (Stato attuale: "
                                + product.getProdStatus() + ")."
                );
            }

            if (item.quantity() > product.getStockQuantity()) {
                throw new CheckoutValidationException(
                        "Stock insufficiente per il prodotto '" + product.getName()
                                + "'. Disponibili: " + product.getStockQuantity()
                                + ", richiesti: " + item.quantity() + "."
                );
            }
        }
    }
}
