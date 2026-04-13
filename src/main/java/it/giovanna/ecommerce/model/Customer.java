package it.giovanna.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of="customerNumber")
public class Customer {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable=false,unique=true)
    private String customerNumber;

    @Column(nullable=false,unique=true)
    private String email;

    @Column(nullable=false)

    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<Address> Addresses;


    @PrePersist
    public void prePersist(){
        this.createdAt=Instant.now();
    }

}
