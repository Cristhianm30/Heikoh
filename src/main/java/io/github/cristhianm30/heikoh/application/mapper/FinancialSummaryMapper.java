package io.github.cristhianm30.heikoh.application.mapper;

import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;
import io.github.cristhianm30.heikoh.domain.model.FinancialSummaryData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FinancialSummaryMapper {

    @Mapping(target = "totalIncome", source = "totalIncome", qualifiedByName = "roundAmount")
    @Mapping(target = "totalExpense", source = "totalExpense", qualifiedByName = "roundAmount")
    @Mapping(target = "totalBalance", source = "totalBalance", qualifiedByName = "roundAmount")
    FinancialSummaryResponse toResponse(FinancialSummaryData data);

    @Named("roundAmount")
    default BigDecimal roundAmount(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}
