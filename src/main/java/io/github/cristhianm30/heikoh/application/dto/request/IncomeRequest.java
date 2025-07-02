package io.github.cristhianm30.heikoh.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class IncomeRequest {
    @NotNull(message = "The amount cannot be null")
    private BigDecimal amount;
    @NotBlank(message = "The description cannot be blank")
    private String description;
    @NotNull(message = "The transactionDate cannot be null")
    private LocalDate transactionDate;
    private String origin;
}
