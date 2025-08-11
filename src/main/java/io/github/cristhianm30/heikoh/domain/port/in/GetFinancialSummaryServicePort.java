package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.FinancialSummaryData;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface GetFinancialSummaryServicePort {
    Mono<FinancialSummaryData> getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate);
}
