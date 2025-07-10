package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;
import io.github.cristhianm30.heikoh.application.mapper.FinancialSummaryMapper;
import io.github.cristhianm30.heikoh.domain.model.FinancialSummaryData;
import io.github.cristhianm30.heikoh.domain.port.in.DashboardServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DashboardServiceImplTest {

    @Mock
    private DashboardServicePort dashboardServicePort;

    @Mock
    private FinancialSummaryMapper financialSummaryMapper;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFinancialSummary_shouldReturnMappedResponse() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        FinancialSummaryData data = FinancialSummaryData.builder()
                .totalIncome(new BigDecimal("1000.00"))
                .totalExpense(new BigDecimal("500.00"))
                .totalBalance(new BigDecimal("500.00"))
                .build();

        FinancialSummaryResponse response = FinancialSummaryResponse.builder()
                .totalIncome(new BigDecimal("1000.00"))
                .totalExpense(new BigDecimal("500.00"))
                .totalBalance(new BigDecimal("500.00"))
                .build();

        when(dashboardServicePort.getFinancialSummary(userId, startDate, endDate)).thenReturn(Mono.just(data));
        when(financialSummaryMapper.toResponse(any(FinancialSummaryData.class))).thenReturn(response);

        Mono<FinancialSummaryResponse> result = dashboardService.getFinancialSummary(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();
    }
}
