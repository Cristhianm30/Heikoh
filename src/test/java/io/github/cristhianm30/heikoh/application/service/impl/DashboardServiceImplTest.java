package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.response.ExpenseAggregationResponse;
import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;
import io.github.cristhianm30.heikoh.application.mapper.IDashboardMapper;
import io.github.cristhianm30.heikoh.domain.model.ExpenseAggregationData;
import io.github.cristhianm30.heikoh.domain.model.FinancialSummaryData;
import io.github.cristhianm30.heikoh.domain.port.in.GetExpenseAggregationServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.GetFinancialSummaryServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DashboardServiceImplTest {

    @Mock
    private GetFinancialSummaryServicePort getFinancialSummaryServicePort;

    @Mock
    private GetExpenseAggregationServicePort getExpenseAggregationServicePort;

    @Mock
    private IDashboardMapper dashboardMapper;

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

        when(getFinancialSummaryServicePort.getFinancialSummary(userId, startDate, endDate)).thenReturn(Mono.just(data));
        when(dashboardMapper.toFinancialSummaryResponse(any(FinancialSummaryData.class))).thenReturn(response);

        Mono<FinancialSummaryResponse> result = dashboardService.getFinancialSummary(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getExpenseAggregation_shouldReturnMappedResponse() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String groupBy = "category";

        ExpenseAggregationData data1 = new ExpenseAggregationData("Food", new BigDecimal("100.00"));
        ExpenseAggregationData data2 = new ExpenseAggregationData("Transport", new BigDecimal("50.00"));
        List<ExpenseAggregationData> dataList = Arrays.asList(data1, data2);

        ExpenseAggregationResponse response1 = new ExpenseAggregationResponse("Food", new BigDecimal("100.00"));
        ExpenseAggregationResponse response2 = new ExpenseAggregationResponse("Transport", new BigDecimal("50.00"));
        List<ExpenseAggregationResponse> responseList = Arrays.asList(response1, response2);

        when(getExpenseAggregationServicePort.getExpenseAggregation(eq(userId), eq(startDate), eq(endDate), eq(groupBy)))
                .thenReturn(Flux.fromIterable(dataList));
        when(dashboardMapper.toExpenseAggregationResponse(data1)).thenReturn(response1);
        when(dashboardMapper.toExpenseAggregationResponse(data2)).thenReturn(response2);

        Flux<ExpenseAggregationResponse> result = dashboardService.getExpenseAggregation(userId, startDate, endDate, groupBy);

        StepVerifier.create(result)
                .expectNext(response1, response2)
                .verifyComplete();
    }
}
