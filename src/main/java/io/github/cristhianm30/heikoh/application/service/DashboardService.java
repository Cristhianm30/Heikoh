package io.github.cristhianm30.heikoh.application.service;

import io.github.cristhianm30.heikoh.application.dto.response.AggregationResponse;
import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface DashboardService {
    Mono<FinancialSummaryResponse> getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate);
    Flux<AggregationResponse> getExpenseAggregation(Long userId, LocalDate startDate, LocalDate endDate, String groupBy);
    Flux<AggregationResponse> getIncomeAggregation(Long userId, LocalDate startDate, LocalDate endDate);
}
