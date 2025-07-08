package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import io.github.cristhianm30.heikoh.infrastructure.mapper.ExpenseEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.ExpenseRepository;
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
class ExpensePersistenceAdapterTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseEntityMapper expenseEntityMapper;

    @InjectMocks
    private ExpensePersistenceAdapter expensePersistenceAdapter;

    private ExpenseModel expenseModel;
    private ExpenseEntity expenseEntity;

    @BeforeEach
    void setUp() {
        expenseModel = ExpenseModel.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now())
                .category("Food")
                .paymentMethod("Credit Card")
                .userId(1L)
                .build();

        expenseEntity = ExpenseEntity.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now())
                .category("Food")
                .paymentMethod("Credit Card")
                .userId(1L)
                .build();
    }

    @Test
    void save_ShouldReturnExpenseModel_WhenSuccessful() {
        when(expenseEntityMapper.toEntity(any(ExpenseModel.class))).thenReturn(expenseEntity);
        when(expenseRepository.save(any(ExpenseEntity.class))).thenReturn(Mono.just(expenseEntity));
        when(expenseEntityMapper.toModel(any(ExpenseEntity.class))).thenReturn(expenseModel);

        StepVerifier.create(expensePersistenceAdapter.save(expenseModel))
                .expectNext(expenseModel)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnExpenseModel_WhenFound() {
        when(expenseRepository.findById(anyLong())).thenReturn(Mono.just(expenseEntity));
        when(expenseEntityMapper.toModel(any(ExpenseEntity.class))).thenReturn(expenseModel);

        StepVerifier.create(expensePersistenceAdapter.findById(1L))
                .expectNext(expenseModel)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        when(expenseRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.findById(1L))
                .expectComplete();
    }

    @Test
    void deleteById_ShouldCompleteSuccessfully() {
        when(expenseRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.deleteById(1L))
                .expectComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetween_ShouldReturnBigDecimal() {
        BigDecimal expectedSum = BigDecimal.valueOf(150.00);
        when(expenseRepository.sumAmountByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Mono.just(expectedSum));

        StepVerifier.create(expensePersistenceAdapter.sumAmountByUserIdAndDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectNext(expectedSum)
                .verifyComplete();
    }

    @Test
    void findByUserIdAndTransactionDateBetween_ShouldReturnFluxOfExpenseModels_WhenFound() {
        when(expenseRepository.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(expenseEntity));
        when(expenseEntityMapper.toModel(any(ExpenseEntity.class))).thenReturn(expenseModel);

        StepVerifier.create(expensePersistenceAdapter.findByUserIdAndTransactionDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectNext(expenseModel)
                .verifyComplete();
    }

    @Test
    void findByUserIdAndTransactionDateBetween_ShouldReturnEmptyFlux_WhenNotFound() {
        when(expenseRepository.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(expensePersistenceAdapter.findByUserIdAndTransactionDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnExpenseModel_WhenFound() {
        when(expenseRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Mono.just(expenseEntity));
        when(expenseEntityMapper.toModel(any(ExpenseEntity.class))).thenReturn(expenseModel);

        StepVerifier.create(expensePersistenceAdapter.findByIdAndUserId(1L, 1L))
                .expectNext(expenseModel)
                .verifyComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnEmptyMono_WhenNotFound() {
        when(expenseRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.findByIdAndUserId(1L, 1L))
                .expectComplete();
    }
}
