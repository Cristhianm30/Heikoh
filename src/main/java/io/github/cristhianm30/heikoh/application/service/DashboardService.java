package io.github.cristhianm30.heikoh.application.service;

import io.github.cristhianm30.heikoh.application.dto.response.ExpenseAggregationResponse;
import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface DashboardService {
    Mono<FinancialSummaryResponse> getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate);
    Flux<ExpenseAggregationResponse> getExpenseAggregation(Long userId, LocalDate startDate, LocalDate endDate, String groupBy);
}
