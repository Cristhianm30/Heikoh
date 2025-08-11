package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.InvalidGroupByException;
import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class GetExpenseAggregationUseCaseTest {

    @Mock
    private ExpenseRepositoryPort expenseRepositoryPort;

    @InjectMocks
    private GetExpenseAggregationUseCase getExpenseAggregationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getExpenseAggregation_byCategoryWithDates_shouldReturnAggregatedData() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String groupBy = "category";

        List<AggregationData> expectedData = Arrays.asList(
                new AggregationData("Food", new BigDecimal("100.00")),
                new AggregationData("Transport", new BigDecimal("50.00"))
        );

        when(expenseRepositoryPort.sumAmountByUserIdAndDateBetweenByCategory(eq(userId), eq(startDate), eq(endDate)))
                .thenReturn(Flux.fromIterable(expectedData));

        Flux<AggregationData> result = getExpenseAggregationUseCase.getExpenseAggregation(userId, startDate, endDate, groupBy);

        StepVerifier.create(result)
                .expectNextSequence(expectedData)
                .verifyComplete();
    }

    @Test
    void getExpenseAggregation_byCategoryWithoutDates_shouldReturnAggregatedData() {
        Long userId = 1L;
        String groupBy = "category";

        List<AggregationData> expectedData = Arrays.asList(
                new AggregationData("Food", new BigDecimal("200.00")),
                new AggregationData("Transport", new BigDecimal("100.00"))
        );

        when(expenseRepositoryPort.sumAmountByUserIdByCategory(eq(userId)))
                .thenReturn(Flux.fromIterable(expectedData));

        Flux<AggregationData> result = getExpenseAggregationUseCase.getExpenseAggregation(userId, null, null, groupBy);

        StepVerifier.create(result)
                .expectNextSequence(expectedData)
                .verifyComplete();
    }

    @Test
    void getExpenseAggregation_byPaymentMethodWithDates_shouldReturnAggregatedData() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String groupBy = "paymentMethod";

        List<AggregationData> expectedData = Arrays.asList(
                new AggregationData("Credit Card", new BigDecimal("150.00")),
                new AggregationData("Cash", new BigDecimal("75.00"))
        );

        when(expenseRepositoryPort.sumAmountByUserIdAndDateBetweenByPaymentMethod(eq(userId), eq(startDate), eq(endDate)))
                .thenReturn(Flux.fromIterable(expectedData));

        Flux<AggregationData> result = getExpenseAggregationUseCase.getExpenseAggregation(userId, startDate, endDate, groupBy);

        StepVerifier.create(result)
                .expectNextSequence(expectedData)
                .verifyComplete();
    }

    @Test
    void getExpenseAggregation_byPaymentMethodWithoutDates_shouldReturnAggregatedData() {
        Long userId = 1L;
        String groupBy = "paymentMethod";

        List<AggregationData> expectedData = Arrays.asList(
                new AggregationData("Credit Card", new BigDecimal("300.00")),
                new AggregationData("Cash", new BigDecimal("150.00"))
        );

        when(expenseRepositoryPort.sumAmountByUserIdByPaymentMethod(eq(userId)))
                .thenReturn(Flux.fromIterable(expectedData));

        Flux<AggregationData> result = getExpenseAggregationUseCase.getExpenseAggregation(userId, null, null, groupBy);

        StepVerifier.create(result)
                .expectNextSequence(expectedData)
                .verifyComplete();
    }

    @Test
    void getExpenseAggregation_invalidGroupBy_shouldReturnError() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String groupBy = "invalid";

        Flux<AggregationData> result = getExpenseAggregationUseCase.getExpenseAggregation(userId, startDate, endDate, groupBy);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidGroupByException &&
                        throwable.getMessage().equals("Invalid groupBy parameter. Must be 'category' or 'paymentMethod'."))
                .verify();
    }
}
