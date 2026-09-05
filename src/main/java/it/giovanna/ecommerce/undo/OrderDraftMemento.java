package it.giovanna.ecommerce.undo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "order_draft_memento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDraftMemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_order_id", nullable = false)
    private Long purchaseOrderId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "state_json", nullable = false, columnDefinition = "jsonb")
    private String stateJson;


    @Column(name = "saved_at", nullable = false)
    private Instant savedAt;
}