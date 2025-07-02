package io.github.cristhianm30.heikoh.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IncomeResponse {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private String origin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
