package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.FinancialSummaryData;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class GetFinancialSummaryUseCaseTest {

    @Mock
    private ExpenseRepositoryPort expenseRepositoryPort;

    @Mock
    private IncomeRepositoryPort incomeRepositoryPort;

    @InjectMocks
    private GetFinancialSummaryUseCase getFinancialSummaryUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFinancialSummary_withDateRange_shouldReturnCorrectSummary() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(incomeRepositoryPort.sumAmountByUserIdAndDateBetween(eq(userId), eq(startDate), eq(endDate)))
                .thenReturn(Mono.just(new BigDecimal("1000.00")));
        when(expenseRepositoryPort.sumAmountByUserIdAndDateBetween(eq(userId), eq(startDate), eq(endDate)))
                .thenReturn(Mono.just(new BigDecimal("500.00")));

        Mono<FinancialSummaryData> result = getFinancialSummaryUseCase.getFinancialSummary(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextMatches(summary ->
                        summary.getTotalIncome().compareTo(new BigDecimal("1000.00")) == 0 &&
                        summary.getTotalExpense().compareTo(new BigDecimal("500.00")) == 0 &&
                        summary.getTotalBalance().compareTo(new BigDecimal("500.00")) == 0)
                .verifyComplete();
    }

    @Test
    void getFinancialSummary_withoutDateRange_shouldReturnCorrectSummary() {
        Long userId = 1L;

        when(incomeRepositoryPort.sumAmountByUserId(eq(userId)))
                .thenReturn(Mono.just(new BigDecimal("2000.00")));
        when(expenseRepositoryPort.sumAmountByUserId(eq(userId)))
                .thenReturn(Mono.just(new BigDecimal("750.00")));

        Mono<FinancialSummaryData> result = getFinancialSummaryUseCase.getFinancialSummary(userId, null, null);

        StepVerifier.create(result)
                .expectNextMatches(summary ->
                        summary.getTotalIncome().compareTo(new BigDecimal("2000.00")) == 0 &&
                        summary.getTotalExpense().compareTo(new BigDecimal("750.00")) == 0 &&
                        summary.getTotalBalance().compareTo(new BigDecimal("1250.00")) == 0)
                .verifyComplete();
    }

    @Test
    void getFinancialSummary_withZeroAmounts_shouldReturnZeroSummary() {
        Long userId = 1L;

        when(incomeRepositoryPort.sumAmountByUserId(eq(userId)))
                .thenReturn(Mono.just(BigDecimal.ZERO));
        when(expenseRepositoryPort.sumAmountByUserId(eq(userId)))
                .thenReturn(Mono.just(BigDecimal.ZERO));

        Mono<FinancialSummaryData> result = getFinancialSummaryUseCase.getFinancialSummary(userId, null, null);

        StepVerifier.create(result)
                .expectNextMatches(summary ->
                        summary.getTotalIncome().compareTo(BigDecimal.ZERO) == 0 &&
                        summary.getTotalExpense().compareTo(BigDecimal.ZERO) == 0 &&
                        summary.getTotalBalance().compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }
}
