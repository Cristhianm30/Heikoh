package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.response.AggregationResponse;
import io.github.cristhianm30.heikoh.application.dto.response.FinancialSummaryResponse;
import io.github.cristhianm30.heikoh.application.mapper.IDashboardMapper;
import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.model.FinancialSummaryData;
import io.github.cristhianm30.heikoh.domain.port.in.GetExpenseAggregationServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.GetFinancialSummaryServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.GetIncomeAggregationServicePort;
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
    private GetIncomeAggregationServicePort getIncomeAggregationServicePort;


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

        AggregationData data1 = new AggregationData("Food", new BigDecimal("100.00"));
        AggregationData data2 = new AggregationData("Transport", new BigDecimal("50.00"));
        List<AggregationData> dataList = Arrays.asList(data1, data2);

        AggregationResponse response1 = new AggregationResponse("Food", new BigDecimal("100.00"));
        AggregationResponse response2 = new AggregationResponse("Transport", new BigDecimal("50.00"));
        List<AggregationResponse> responseList = Arrays.asList(response1, response2);

        when(getExpenseAggregationServicePort.getExpenseAggregation(eq(userId), eq(startDate), eq(endDate), eq(groupBy)))
                .thenReturn(Flux.fromIterable(dataList));
        when(dashboardMapper.toAggregationResponse(any(AggregationData.class)))
                .thenAnswer(invocation -> {
                    AggregationData arg = invocation.getArgument(0);
                    if (arg.getKey().equals("Food")) return response1;
                    if (arg.getKey().equals("Transport")) return response2;
                    return null;
                });

        Flux<AggregationResponse> result = dashboardService.getExpenseAggregation(userId, startDate, endDate, groupBy);

        StepVerifier.create(result)
                .expectNext(response1, response2)
                .verifyComplete();
    }

    @Test
    void getIncomeAggregation_shouldReturnMappedResponse() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        AggregationData data1 = new AggregationData("Salary", new BigDecimal("1000.00"));
        AggregationData data2 = new AggregationData("Freelance", new BigDecimal("500.00"));
        List<AggregationData> dataList = Arrays.asList(data1, data2);

        AggregationResponse response1 = new AggregationResponse("Salary", new BigDecimal("1000.00"));
        AggregationResponse response2 = new AggregationResponse("Freelance", new BigDecimal("500.00"));

        when(getIncomeAggregationServicePort.getIncomeAggregation(eq(userId), eq(startDate), eq(endDate)))
                .thenReturn(Flux.fromIterable(dataList));
        when(dashboardMapper.toAggregationResponse(any(AggregationData.class)))
                .thenAnswer(invocation -> {
                    AggregationData arg = invocation.getArgument(0);
                    if (arg.getKey().equals("Salary")) return response1;
                    if (arg.getKey().equals("Freelance")) return response2;
                    return null;
                });

        Flux<AggregationResponse> result = dashboardService.getIncomeAggregation(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNext(response1, response2)
                .verifyComplete();
    }
}
