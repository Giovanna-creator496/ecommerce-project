package it.giovanna.ecommerce.fulfillment;

import it.giovanna.ecommerce.model.OrderLineItem;
import it.giovanna.ecommerce.model.Product;
import it.giovanna.ecommerce.model.PurchaseOrder;
import it.giovanna.ecommerce.model.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    public void deductInventory(PurchaseOrder order) {

        for (OrderLineItem item : order.getOrderLineItems()) {

            Product product = item.getProduct();

            if (product == null) {
                throw new IllegalStateException(
                        "La riga d'ordine " + item.getId()
                                + " non è associata ad alcun prodotto"
                );
            }

            if (product.getProdStatus() == ProductStatus.DISCONTINUED) {
                throw new IllegalStateException(
                        "Il prodotto " + product.getSku()
                                + " è discontinuato"
                );
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException(
                        "Stock insufficiente per il prodotto "
                                + product.getSku()
                );
            }

            product.setStockQuantity(
                    product.getStockQuantity() - item.getQuantity()
            );
        }
    }
}
