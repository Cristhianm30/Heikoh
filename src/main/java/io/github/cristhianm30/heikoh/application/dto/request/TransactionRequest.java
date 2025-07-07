package io.github.cristhianm30.heikoh.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
    @Min(value = 1900, message = "Year must be at least 1900")
    private Integer year;
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;
    @Min(value = 1, message = "Limit must be at least 1")
    private Integer limit;
    @Min(value = 0, message = "Offset must be at least 0")
    private Integer offset;
    @Pattern(regexp = "^(income|expense)$", message = "Type must be 'income' or 'expense'")
    private String type;
}
