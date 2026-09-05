package it.giovanna.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name="product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

@EqualsAndHashCode(of="sku", callSuper = false)
public class Product extends AuditableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    /*@Column(nullable = false)
    private boolean active;*/

    @Enumerated(EnumType.STRING)
    @Column(name="prod_status")
    private ProductStatus prodStatus;

    @Column(nullable=false)
    @Embedded
    private Money price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private Set<ProductCategory> productCategories;
}
