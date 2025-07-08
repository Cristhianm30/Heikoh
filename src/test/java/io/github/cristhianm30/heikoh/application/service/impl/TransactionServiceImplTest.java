package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionsRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionsResponse;
import io.github.cristhianm30.heikoh.application.mapper.TransactionMapper;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionsData;
import io.github.cristhianm30.heikoh.domain.port.in.TransactionServicePort;
import io.github.cristhianm30.heikoh.domain.exception.TransactionNotFoundException;
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

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.EXPENSE_NOT_FOUND;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INCOME_NOT_FOUND;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INVALID_TRANSACTION_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionServicePort transactionServicePort;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Long userId;
    private ExpenseModel expenseModel;
    private IncomeModel incomeModel;
    private TransactionsResponse expenseTransactionsResponse;
    private TransactionsResponse incomeTransactionsResponse;
    private TransactionsRequest transactionsRequest;
    private TransactionsData transactionsData;
    private TransactionResponse expenseTransactionResponse;
    private TransactionResponse incomeTransactionResponse;

    @BeforeEach
    void setUp() {
        userId = 1L;

        expenseModel = ExpenseModel.builder()
                .id(1L)
                .userId(userId)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now().minusDays(1))
                .build();

        incomeModel = IncomeModel.builder()
                .id(2L)
                .userId(userId)
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .build();

        expenseTransactionsResponse = TransactionsResponse.builder()
                .type("Gasto")
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now().minusDays(1))
                .build();

        incomeTransactionsResponse = TransactionsResponse.builder()
                .type("Ingreso")
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .build();

        transactionsRequest = TransactionsRequest.builder()
                .year(LocalDate.now().getYear())
                .month(LocalDate.now().getMonthValue())
                .limit(10)
                .offset(0)
                .build();
        
        transactionsData = new TransactionsData(
                transactionsRequest.getYear(),
                transactionsRequest.getMonth(),
                transactionsRequest.getLimit(),
                transactionsRequest.getOffset(),
                transactionsRequest.getType()
        );

        expenseTransactionResponse = TransactionResponse.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .date(LocalDate.now().minusDays(1))
                .type("Gasto")
                .build();

        incomeTransactionResponse = TransactionResponse.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .date(LocalDate.now())
                .type("Ingreso")
                .build();
    }

    @Test
    void getTransactions_ShouldReturnMappedTransactions() {
        // Arrange
        when(transactionMapper.toTransactionData(transactionsRequest)).thenReturn(transactionsData);
        when(transactionServicePort.getTransactions(userId, transactionsData))
                .thenReturn(Flux.just(incomeModel, expenseModel));
        when(transactionMapper.toTransactionsResponse(incomeModel)).thenReturn(incomeTransactionsResponse);
        when(transactionMapper.toTransactionsResponse(expenseModel)).thenReturn(expenseTransactionsResponse);

        // Act & Assert
        StepVerifier.create(transactionService.getTransactions(userId, transactionsRequest))
                .expectNext(incomeTransactionsResponse)
                .expectNext(expenseTransactionsResponse)
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldReturnEmptyFlux_WhenNoTransactionsFound() {
        // Arrange
        when(transactionMapper.toTransactionData(transactionsRequest)).thenReturn(transactionsData);
        when(transactionServicePort.getTransactions(userId, transactionsData)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(transactionService.getTransactions(userId, transactionsRequest))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void getTransactionDetail_ShouldReturnExpenseTransactionResponse_WhenExpenseFound() {
        // Arrange
        when(transactionServicePort.getTransactionDetail(anyLong(), anyLong(), anyString()))
                .thenReturn(Mono.just(expenseModel));
        when(transactionMapper.toTransactionResponse(any(ExpenseModel.class)))
                .thenReturn(expenseTransactionResponse);

        // Act & Assert
        StepVerifier.create(transactionService.getTransactionDetail(userId, 1L, "expense"))
                .expectNext(expenseTransactionResponse)
                .verifyComplete();
    }

    @Test
    void getTransactionDetail_ShouldReturnIncomeTransactionResponse_WhenIncomeFound() {
        // Arrange
        when(transactionServicePort.getTransactionDetail(anyLong(), anyLong(), anyString()))
                .thenReturn(Mono.just(incomeModel));
        when(transactionMapper.toTransactionResponse(any(IncomeModel.class)))
                .thenReturn(incomeTransactionResponse);

        // Act & Assert
        StepVerifier.create(transactionService.getTransactionDetail(userId, 2L, "income"))
                .expectNext(incomeTransactionResponse)
                .verifyComplete();
    }

    @Test
    void getTransactionDetail_ShouldThrowTransactionNotFoundException_WhenTransactionNotFound() {
        // Arrange
        when(transactionServicePort.getTransactionDetail(anyLong(), anyLong(), anyString()))
                .thenReturn(Mono.error(new TransactionNotFoundException(EXPENSE_NOT_FOUND + 1L)));

        // Act & Assert
        StepVerifier.create(transactionService.getTransactionDetail(userId, 1L, "expense"))
                .expectErrorMatches(throwable ->
                        throwable instanceof TransactionNotFoundException &&
                                throwable.getMessage().contains(EXPENSE_NOT_FOUND.replace("%s", "")))
                .verify();
    }

    @Test
    void getTransactionDetail_ShouldThrowTransactionNotFoundException_WhenInvalidType() {
        // Arrange
        when(transactionServicePort.getTransactionDetail(anyLong(), anyLong(), anyString()))
                .thenReturn(Mono.error(new TransactionNotFoundException(INVALID_TRANSACTION_TYPE + "invalid")));

        // Act & Assert
        StepVerifier.create(transactionService.getTransactionDetail(userId, 1L, "invalid"))
                .expectErrorMatches(throwable ->
                        throwable instanceof TransactionNotFoundException &&
                                throwable.getMessage().contains(INVALID_TRANSACTION_TYPE.replace("%s", "")))
                .verify();
    }
}