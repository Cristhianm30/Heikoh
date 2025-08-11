package io.github.cristhianm30.heikoh.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private String type;
}
