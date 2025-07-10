package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.response.ExpenseAggregationResponse;
import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;
import io.github.cristhianm30.heikoh.application.mapper.IDashboardMapper;
import io.github.cristhianm30.heikoh.application.service.DashboardService;
import io.github.cristhianm30.heikoh.domain.port.in.GetExpenseAggregationServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.GetFinancialSummaryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final GetFinancialSummaryServicePort getFinancialSummaryServicePort;
    private final GetExpenseAggregationServicePort getExpenseAggregationServicePort;
    private final IDashboardMapper dashboardMapper;

    @Override
    public Mono<FinancialSummaryResponse> getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        return getFinancialSummaryServicePort.getFinancialSummary(userId, startDate, endDate)
                .map(dashboardMapper::toFinancialSummaryResponse);
    }

    @Override
    public Flux<ExpenseAggregationResponse> getExpenseAggregation(Long userId, LocalDate startDate, LocalDate endDate, String groupBy) {
        return getExpenseAggregationServicePort.getExpenseAggregation(userId, startDate, endDate, groupBy)
                .map(dashboardMapper::toExpenseAggregationResponse);
    }
}
