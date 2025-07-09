package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.infrastructure.entity.IncomeEntity;
import io.github.cristhianm30.heikoh.infrastructure.mapper.IncomeEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        BigDecimal expectedSum = BigDecimal.valueOf(200.00);
        when(incomeRepository.sumAmountByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Mono.just(expectedSum));

        StepVerifier.create(incomePersistenceAdapter.sumAmountByUserIdAndDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectNext(expectedSum)
                .verifyComplete();
    }

    @Test
    void findByUserIdAndTransactionDateBetween_ShouldReturnFluxOfIncomeModels_WhenFound() {
        when(incomeRepository.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(incomeEntity));
        when(incomeEntityMapper.toModel(any(IncomeEntity.class))).thenReturn(incomeModel);

        StepVerifier.create(incomePersistenceAdapter.findByUserIdAndTransactionDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectNext(incomeModel)
                .verifyComplete();
    }

    @Test
    void findByUserIdAndTransactionDateBetween_ShouldReturnEmptyFlux_WhenNotFound() {
        when(incomeRepository.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(incomePersistenceAdapter.findByUserIdAndTransactionDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnIncomeModel_WhenFound() {
        when(incomeRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Mono.just(incomeEntity));
        when(incomeEntityMapper.toModel(any(IncomeEntity.class))).thenReturn(incomeModel);

        StepVerifier.create(incomePersistenceAdapter.findByIdAndUserId(1L, 1L))
                .expectNext(incomeModel)
                .verifyComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnEmptyMono_WhenNotFound() {
        when(incomeRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(incomePersistenceAdapter.findByIdAndUserId(1L, 1L))
                .expectComplete();
    }

    @Test
    void deleteByIdAndUserId_ShouldCompleteSuccessfully() {
        when(incomeRepository.deleteByIdAndUserId(anyLong(), anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(incomePersistenceAdapter.deleteByIdAndUserId(1L, 1L))
                .expectComplete();
    }
}
