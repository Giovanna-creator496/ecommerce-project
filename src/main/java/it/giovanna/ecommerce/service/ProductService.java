package it.giovanna.ecommerce.service;

import it.giovanna.ecommerce.dto.ProductUpdateDTO;
import it.giovanna.ecommerce.model.Product;
import it.giovanna.ecommerce.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    @Transactional
    public Product updateProduct(Long id, ProductUpdateDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + id)
                );


        if (!product.getVersion().equals(dto.getVersion())) {
            throw new OptimisticLockException(
                    "Product has been modified by another user"
            );
        }


        product.setName(dto.getName());


        return productRepository.saveAndFlush(product);
    }
}
