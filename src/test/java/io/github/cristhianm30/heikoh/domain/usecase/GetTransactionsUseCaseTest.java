package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionRequest;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionData;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionsUseCaseTest {

    @Mock
    private ExpenseRepositoryPort expenseRepositoryPort;

    @Mock
    private IncomeRepositoryPort incomeRepositoryPort;

    @InjectMocks
    private GetTransactionsUseCase getTransactionsUseCase;

    private Long userId;
    private ExpenseModel expense1;
    private ExpenseModel expense2;
    private IncomeModel income1;
    private IncomeModel income2;
    private TransactionData defaultRequest;
    private TransactionData yearRequest;
    private TransactionData yearMonthRequest;
    private TransactionData paginatedRequest;
    private TransactionData incomeTypeRequest;
    private TransactionData expenseTypeRequest;

    @BeforeEach
    void setUp() {
        userId = 1L;

        expense1 = ExpenseModel.builder()
                .id(1L)
                .userId(userId)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.of(2023, 1, 15))
                .build();

        expense2 = ExpenseModel.builder()
                .id(2L)
                .userId(userId)
                .amount(BigDecimal.valueOf(20.00))
                .description("Coffee")
                .transactionDate(LocalDate.of(2023, 1, 10))
                .build();

        income1 = IncomeModel.builder()
                .id(3L)
                .userId(userId)
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.of(2023, 1, 20))
                .build();

        income2 = IncomeModel.builder()
                .id(4L)
                .userId(userId)
                .amount(BigDecimal.valueOf(30.00))
                .description("Gift")
                .transactionDate(LocalDate.of(2023, 1, 5))
                .build();

        defaultRequest = TransactionData.builder().build();
        yearRequest = TransactionData.builder().year(2023).build();
        yearMonthRequest = TransactionData.builder().year(2023).month(1).build();
        paginatedRequest = TransactionData.builder().limit(2).offset(1).build();
        incomeTypeRequest = TransactionData.builder().type("income").build();
        expenseTypeRequest = TransactionData.builder().type("expense").build();
    }

    @Test
    void getTransactions_ShouldReturnAllTransactionsSortedByDateDesc() {
        when(expenseRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(expense1, expense2));
        when(incomeRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(income1, income2));

        StepVerifier.create(getTransactionsUseCase.getTransactions(userId, defaultRequest))
                .expectNext(income1, expense1, expense2, income2)
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldApplyLimitAndOffset() {
        when(expenseRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(expense1, expense2));
        when(incomeRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(income1, income2));

        StepVerifier.create(getTransactionsUseCase.getTransactions(userId, paginatedRequest))
                .expectNext(expense1, expense2)
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldFilterByYear() {
        when(expenseRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(expense1, expense2));
        when(incomeRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(income1, income2));

        StepVerifier.create(getTransactionsUseCase.getTransactions(userId, yearRequest))
                .expectNext(income1, expense1, expense2, income2)
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldFilterByYearAndMonth() {
        when(expenseRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(expense1, expense2));
        when(incomeRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(income1, income2));

        StepVerifier.create(getTransactionsUseCase.getTransactions(userId, yearMonthRequest))
                .expectNext(income1, expense1, expense2, income2)
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldFilterByIncomeType() {
        when(incomeRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(income1, income2));

        StepVerifier.create(getTransactionsUseCase.getTransactions(userId, incomeTypeRequest))
                .expectNext(income1, income2)
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldFilterByExpenseType() {
        when(expenseRepositoryPort.findByUserIdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(expense1, expense2));

        StepVerifier.create(getTransactionsUseCase.getTransactions(userId, expenseTypeRequest))
                .expectNext(expense1, expense2)
                .verifyComplete();
    }
}
