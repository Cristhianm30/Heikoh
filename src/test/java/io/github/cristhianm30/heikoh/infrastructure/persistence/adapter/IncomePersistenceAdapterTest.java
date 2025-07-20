package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.infrastructure.entity.AggregationResult;
import io.github.cristhianm30.heikoh.infrastructure.entity.IncomeEntity;
import io.github.cristhianm30.heikoh.infrastructure.mapper.IncomeEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomePersistenceAdapterTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeEntityMapper incomeEntityMapper;

    @InjectMocks
    private IncomePersistenceAdapter incomePersistenceAdapter;

    private IncomeModel incomeModel;
    private IncomeEntity incomeEntity;

    @BeforeEach
    void setUp() {
        incomeModel = IncomeModel.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .origin("Work")
                .userId(1L)
                .build();

        incomeEntity = IncomeEntity.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .origin("Work")
                .userId(1L)
                .build();
    }

    @Test
    void save_ShouldReturnIncomeModel_WhenSuccessful() {
        when(incomeEntityMapper.toEntity(any(IncomeModel.class))).thenReturn(incomeEntity);
        when(incomeRepository.save(any(IncomeEntity.class))).thenReturn(Mono.just(incomeEntity));
        when(incomeEntityMapper.toModel(any(IncomeEntity.class))).thenReturn(incomeModel);

        StepVerifier.create(incomePersistenceAdapter.save(incomeModel))
                .expectNext(incomeModel)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnIncomeModel_WhenFound() {
        when(incomeRepository.findById(anyLong())).thenReturn(Mono.just(incomeEntity));
        when(incomeEntityMapper.toModel(any(IncomeEntity.class))).thenReturn(incomeModel);

        StepVerifier.create(incomePersistenceAdapter.findById(1L))
                .expectNext(incomeModel)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        when(incomeRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(incomePersistenceAdapter.findById(1L))
                .expectComplete();
    }

    @Test
    void deleteById_ShouldCompleteSuccessfully() {
        when(incomeRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(incomePersistenceAdapter.deleteById(1L))
                .expectComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetween_ShouldReturnBigDecimal() {
        BigDecimal expectedSum = BigDecimal.valueOf(150.00);
        when(incomeRepository.sumAmountByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Mono.just(expectedSum));

        StepVerifier.create(incomePersistenceAdapter.sumAmountByUserIdAndDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectNext(expectedSum)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetween_ShouldReturnZeroWhenNoIncomes() {
        when(incomeRepository.sumAmountByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(incomePersistenceAdapter.sumAmountByUserIdAndDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectNext(BigDecimal.ZERO)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserId_ShouldReturnBigDecimal() {
        BigDecimal expectedSum = BigDecimal.valueOf(200.00);
        when(incomeRepository.sumAmountByUserId(anyLong()))
                .thenReturn(Mono.just(expectedSum));

        StepVerifier.create(incomePersistenceAdapter.sumAmountByUserId(1L))
                .expectNext(expectedSum)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserId_ShouldReturnZeroWhenNoIncomes() {
        when(incomeRepository.sumAmountByUserId(anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(incomePersistenceAdapter.sumAmountByUserId(1L))
                .expectNext(BigDecimal.ZERO)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetweenByOrigin_ShouldReturnAggregatedData() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Salary", new BigDecimal("1000.00")),
                new AggregationResult("Freelance", new BigDecimal("500.00"))
        );
        when(incomeRepository.sumAmountByUserIdAndTransactionDateBetweenByOrigin(userId, startDate, endDate))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = incomePersistenceAdapter.sumAmountByUserIdAndDateBetweenByOrigin(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Salary") && data.getTotalAmount().compareTo(new BigDecimal("1000.00")) == 0)
                .expectNextMatches(data -> data.getKey().equals("Freelance") && data.getTotalAmount().compareTo(new BigDecimal("500.00")) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdByOrigin_ShouldReturnAggregatedData() {
        Long userId = 1L;
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Salary", new BigDecimal("2000.00")),
                new AggregationResult("Freelance", new BigDecimal("1000.00"))
        );
        when(incomeRepository.sumAmountByUserIdByOrigin(userId))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = incomePersistenceAdapter.sumAmountByUserIdByOrigin(userId);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Salary") && data.getTotalAmount().compareTo(new BigDecimal("2000.00")) == 0)
                .expectNextMatches(data -> data.getKey().equals("Freelance") && data.getTotalAmount().compareTo(new BigDecimal("1000.00")) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetweenByOrigin_ShouldHandleNullTotalAmount() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Salary", null)
        );
        when(incomeRepository.sumAmountByUserIdAndTransactionDateBetweenByOrigin(userId, startDate, endDate))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = incomePersistenceAdapter.sumAmountByUserIdAndDateBetweenByOrigin(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Salary") && data.getTotalAmount().compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdByOrigin_ShouldHandleNullTotalAmount() {
        Long userId = 1L;
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Salary", null)
        );
        when(incomeRepository.sumAmountByUserIdByOrigin(userId))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = incomePersistenceAdapter.sumAmountByUserIdByOrigin(userId);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Salary") && data.getTotalAmount().compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }
}