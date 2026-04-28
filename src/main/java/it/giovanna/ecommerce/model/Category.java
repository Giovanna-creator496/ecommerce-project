package it.giovanna.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name="category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of={"name","parentCategory"})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_category_id",nullable = false)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private Set<Category> subCategories;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private Set<ProductCategory> productCategories;


}
