package io.github.cristhianm30.heikoh.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequest {
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private String category;
    private String paymentMethod;
}
