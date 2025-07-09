package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;
import io.github.cristhianm30.heikoh.application.mapper.FinancialSummaryMapper;
import io.github.cristhianm30.heikoh.application.service.DashboardService;
import io.github.cristhianm30.heikoh.domain.port.in.DashboardServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardServicePort dashboardServicePort;
    private final FinancialSummaryMapper financialSummaryMapper;

    @Override
    public Mono<FinancialSummaryResponse> getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        return dashboardServicePort.getFinancialSummary(userId, startDate, endDate)
                .map(financialSummaryMapper::toResponse);
    }
}
