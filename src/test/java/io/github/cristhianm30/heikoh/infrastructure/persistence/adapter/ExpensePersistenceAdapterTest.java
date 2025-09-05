package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.infrastructure.entity.AggregationResult;
import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import io.github.cristhianm30.heikoh.infrastructure.mapper.ExpenseEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.ExpenseRepository;
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
    void sumAmountByUserIdAndDateBetween_ShouldReturnZeroWhenNoExpenses() {
        when(expenseRepository.sumAmountByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.sumAmountByUserIdAndDateBetween(1L, LocalDate.now().minusDays(7), LocalDate.now()))
                .expectNext(BigDecimal.ZERO)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserId_ShouldReturnBigDecimal() {
        BigDecimal expectedSum = BigDecimal.valueOf(200.00);
        when(expenseRepository.sumAmountByUserId(anyLong()))
                .thenReturn(Mono.just(expectedSum));

        StepVerifier.create(expensePersistenceAdapter.sumAmountByUserId(1L))
                .expectNext(expectedSum)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserId_ShouldReturnZeroWhenNoExpenses() {
        when(expenseRepository.sumAmountByUserId(anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.sumAmountByUserId(1L))
                .expectNext(BigDecimal.ZERO)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetweenByCategory_ShouldReturnAggregatedData() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Food", new BigDecimal("100.00")),
                new AggregationResult("Transport", new BigDecimal("50.00"))
        );
        when(expenseRepository.sumAmountByUserIdAndTransactionDateBetweenByCategory(userId, startDate, endDate))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = expensePersistenceAdapter.sumAmountByUserIdAndDateBetweenByCategory(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Food") && data.getTotalAmount().compareTo(new BigDecimal("100.00")) == 0)
                .expectNextMatches(data -> data.getKey().equals("Transport") && data.getTotalAmount().compareTo(new BigDecimal("50.00")) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdByCategory_ShouldReturnAggregatedData() {
        Long userId = 1L;
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Food", new BigDecimal("200.00")),
                new AggregationResult("Transport", new BigDecimal("100.00"))
        );
        when(expenseRepository.sumAmountByUserIdByCategory(userId))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = expensePersistenceAdapter.sumAmountByUserIdByCategory(userId);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Food") && data.getTotalAmount().compareTo(new BigDecimal("200.00")) == 0)
                .expectNextMatches(data -> data.getKey().equals("Transport") && data.getTotalAmount().compareTo(new BigDecimal("100.00")) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetweenByPaymentMethod_ShouldReturnAggregatedData() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Credit Card", new BigDecimal("150.00")),
                new AggregationResult("Cash", new BigDecimal("75.00"))
        );
        when(expenseRepository.sumAmountByUserIdAndTransactionDateBetweenByPaymentMethod(userId, startDate, endDate))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = expensePersistenceAdapter.sumAmountByUserIdAndDateBetweenByPaymentMethod(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Credit Card") && data.getTotalAmount().compareTo(new BigDecimal("150.00")) == 0)
                .expectNextMatches(data -> data.getKey().equals("Cash") && data.getTotalAmount().compareTo(new BigDecimal("75.00")) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdByPaymentMethod_ShouldReturnAggregatedData() {
        Long userId = 1L;
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Credit Card", new BigDecimal("300.00")),
                new AggregationResult("Cash", new BigDecimal("150.00"))
        );
        when(expenseRepository.sumAmountByUserIdByPaymentMethod(userId))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = expensePersistenceAdapter.sumAmountByUserIdByPaymentMethod(userId);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Credit Card") && data.getTotalAmount().compareTo(new BigDecimal("300.00")) == 0)
                .expectNextMatches(data -> data.getKey().equals("Cash") && data.getTotalAmount().compareTo(new BigDecimal("150.00")) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetweenByCategory_ShouldHandleNullTotalAmount() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Food", null)
        );
        when(expenseRepository.sumAmountByUserIdAndTransactionDateBetweenByCategory(userId, startDate, endDate))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = expensePersistenceAdapter.sumAmountByUserIdAndDateBetweenByCategory(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Food") && data.getTotalAmount().compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdByCategory_ShouldHandleNullTotalAmount() {
        Long userId = 1L;
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Food", null)
        );
        when(expenseRepository.sumAmountByUserIdByCategory(userId))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = expensePersistenceAdapter.sumAmountByUserIdByCategory(userId);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Food") && data.getTotalAmount().compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }

    @Test
    void sumAmountByUserIdAndDateBetweenByPaymentMethod_ShouldHandleNullTotalAmount() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<AggregationResult> results = Arrays.asList(
                new AggregationResult("Credit Card", null)
        );
        when(expenseRepository.sumAmountByUserIdAndTransactionDateBetweenByPaymentMethod(userId, startDate, endDate))
                .thenReturn(Flux.fromIterable(results));

        Flux<AggregationData> result = expensePersistenceAdapter.sumAmountByUserIdAndDateBetweenByPaymentMethod(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNextMatches(data -> data.getKey().equals("Credit Card") && data.getTotalAmount().compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }

    @Test
    void findByUserIdAndTransactionDateBetween_ShouldReturnExpenseModels_WhenFound() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        ExpenseEntity expenseEntity1 = ExpenseEntity.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.of(2024, 1, 15))
                .category("Food")
                .paymentMethod("Credit Card")
                .userId(userId)
                .build();
        
        ExpenseEntity expenseEntity2 = ExpenseEntity.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(30.00))
                .description("Transport")
                .transactionDate(LocalDate.of(2024, 1, 20))
                .category("Transport")
                .paymentMethod("Cash")
                .userId(userId)
                .build();
        
        ExpenseModel expenseModel1 = ExpenseModel.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.of(2024, 1, 15))
                .category("Food")
                .paymentMethod("Credit Card")
                .userId(userId)
                .build();
        
        ExpenseModel expenseModel2 = ExpenseModel.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(30.00))
                .description("Transport")
                .transactionDate(LocalDate.of(2024, 1, 20))
                .category("Transport")
                .paymentMethod("Cash")
                .userId(userId)
                .build();
        
        List<ExpenseEntity> entities = Arrays.asList(expenseEntity1, expenseEntity2);
        
        when(expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate))
                .thenReturn(Flux.fromIterable(entities));
        when(expenseEntityMapper.toModel(expenseEntity1)).thenReturn(expenseModel1);
        when(expenseEntityMapper.toModel(expenseEntity2)).thenReturn(expenseModel2);

        Flux<ExpenseModel> result = expensePersistenceAdapter.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectNext(expenseModel1)
                .expectNext(expenseModel2)
                .verifyComplete();
    }

    @Test
    void findByUserIdAndTransactionDateBetween_ShouldReturnEmpty_WhenNoExpensesFound() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate))
                .thenReturn(Flux.empty());

        Flux<ExpenseModel> result = expensePersistenceAdapter.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

        StepVerifier.create(result)
                .expectComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnExpenseModel_WhenFound() {
        Long id = 1L;
        Long userId = 1L;
        
        when(expenseRepository.findByIdAndUserId(id, userId)).thenReturn(Mono.just(expenseEntity));
        when(expenseEntityMapper.toModel(expenseEntity)).thenReturn(expenseModel);

        StepVerifier.create(expensePersistenceAdapter.findByIdAndUserId(id, userId))
                .expectNext(expenseModel)
                .verifyComplete();
    }

    @Test
    void findByIdAndUserId_ShouldReturnEmpty_WhenNotFound() {
        Long id = 1L;
        Long userId = 1L;
        
        when(expenseRepository.findByIdAndUserId(id, userId)).thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.findByIdAndUserId(id, userId))
                .expectComplete();
    }

    @Test
    void deleteByIdAndUserId_ShouldCompleteSuccessfully_WhenExpenseExists() {
        Long id = 1L;
        Long userId = 1L;
        
        when(expenseRepository.deleteByIdAndUserId(id, userId)).thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.deleteByIdAndUserId(id, userId))
                .expectComplete();
    }

    @Test
    void deleteByIdAndUserId_ShouldCompleteSuccessfully_WhenExpenseDoesNotExist() {
        Long id = 999L;
        Long userId = 1L;
        
        when(expenseRepository.deleteByIdAndUserId(id, userId)).thenReturn(Mono.empty());

        StepVerifier.create(expensePersistenceAdapter.deleteByIdAndUserId(id, userId))
                .expectComplete();
    }

}

