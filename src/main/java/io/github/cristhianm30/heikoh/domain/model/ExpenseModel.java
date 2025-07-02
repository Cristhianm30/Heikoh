package io.github.cristhianm30.heikoh.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseModel {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private String category;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}