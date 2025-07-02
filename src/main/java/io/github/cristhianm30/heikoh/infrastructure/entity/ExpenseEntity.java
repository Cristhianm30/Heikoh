package io.github.cristhianm30.heikoh.infrastructure.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("expenses")
public class ExpenseEntity {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("amount")
    private BigDecimal amount;

    @Column("description")
    private String description;

    @Column("transaction_date")
    private LocalDate transactionDate;

    @Column("category")
    private String category;

    @Column("payment_method")
    private String paymentMethod;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}