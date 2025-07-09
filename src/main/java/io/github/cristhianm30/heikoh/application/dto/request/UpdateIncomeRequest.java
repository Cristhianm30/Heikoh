package io.github.cristhianm30.heikoh.application.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateIncomeRequest {
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private String source;
}