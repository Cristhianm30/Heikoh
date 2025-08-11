package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.port.in.GetIncomeAggregationServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class GetIncomeAggregationUseCaseTest {

    @Mock
    private IncomeRepositoryPort incomeRepositoryPort;

    @InjectMocks
    private GetIncomeAggregationUseCase getIncomeAggregationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getIncomeAggregation_withDates_shouldReturnAggregatedData() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<AggregationData> expectedData = Arrays.asList(
                new AggregationData("Salary", new BigDecimal("1000.00")),
                new AggregationData("Freelance", new BigDecimal("500.00"))
        );

        when(incomeRepositoryPort.sumAmountByUserIdAndDateBetweenByOrigin(eq(userId), eq(startDate), eq(endDate)))
                .thenReturn(Flux.fromIterable(expectedData));

        Flux<AggregationData> result = getIncomeAggregationUseCase.getIncomeAggregation(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextSequence(expectedData)
                .verifyComplete();
    }

    @Test
    void getIncomeAggregation_withoutDates_shouldReturnAggregatedData() {
        Long userId = 1L;

        List<AggregationData> expectedData = Arrays.asList(
                new AggregationData("Salary", new BigDecimal("2000.00")),
                new AggregationData("Freelance", new BigDecimal("1000.00"))
        );

        when(incomeRepositoryPort.sumAmountByUserIdByOrigin(eq(userId)))
                .thenReturn(Flux.fromIterable(expectedData));

        Flux<AggregationData> result = getIncomeAggregationUseCase.getIncomeAggregation(userId, null, null);

        StepVerifier.create(result)
                .expectNextSequence(expectedData)
                .verifyComplete();
    }
}
