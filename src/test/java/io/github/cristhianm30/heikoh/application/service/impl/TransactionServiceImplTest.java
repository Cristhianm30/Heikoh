package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionsRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterIncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.request.UpdateExpenseRequest;
import io.github.cristhianm30.heikoh.application.dto.request.UpdateIncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionsResponse;
import io.github.cristhianm30.heikoh.application.mapper.TransactionMapper;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionsData;
import io.github.cristhianm30.heikoh.domain.port.in.TransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.UpdateTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.DeleteTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.exception.TransactionNotFoundException;
import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionServicePort transactionServicePort;

    @Mock
    private RegisterTransactionServicePort registerTransactionServicePort;

    @Mock
    private UpdateTransactionServicePort updateTransactionServicePort;

    @Mock
    private DeleteTransactionServicePort deleteTransactionServicePort;

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

    @Test
    void registerTransaction_ShouldSuccessfullyRegisterExpense() {
        // Arrange
        io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest registerExpenseRequest = io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest.builder().amount(BigDecimal.TEN).build();
        TransactionResponse expectedResponse = TransactionResponse.builder().id(1L).type("Gasto").build();

        when(transactionMapper.toExpenseModel(any(io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest.class))).thenReturn(expenseModel);
        when(registerTransactionServicePort.registerTransaction(anyLong(), eq("expense"), any(Object.class))).thenReturn(Mono.just(expenseModel));
        when(transactionMapper.toTransactionResponse(any(ExpenseModel.class))).thenReturn(expectedResponse);

        // Act & Assert
        StepVerifier.create(transactionService.registerTransaction(userId, "expense", registerExpenseRequest))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void registerTransaction_ShouldSuccessfullyRegisterIncome() {
        // Arrange
        io.github.cristhianm30.heikoh.application.dto.request.RegisterIncomeRequest registerIncomeRequest = io.github.cristhianm30.heikoh.application.dto.request.RegisterIncomeRequest.builder().amount(BigDecimal.TEN).build();
        TransactionResponse expectedResponse = TransactionResponse.builder().id(1L).type("Ingreso").build();

        when(transactionMapper.toIncomeModel(any(io.github.cristhianm30.heikoh.application.dto.request.RegisterIncomeRequest.class))).thenReturn(incomeModel);
        when(registerTransactionServicePort.registerTransaction(anyLong(), eq("income"), any(Object.class))).thenReturn(Mono.just(incomeModel));
        when(transactionMapper.toTransactionResponse(any(IncomeModel.class))).thenReturn(expectedResponse);

        // Act & Assert
        StepVerifier.create(transactionService.registerTransaction(userId, "income", registerIncomeRequest))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void registerTransaction_ShouldThrowInvalidTransactionTypeException() {
        // Arrange
        io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest registerExpenseRequest = io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest.builder().amount(BigDecimal.TEN).build();

        // Act & Assert
        StepVerifier.create(transactionService.registerTransaction(userId, "invalid", registerExpenseRequest))
                .expectErrorMatches(throwable -> throwable instanceof InvalidTransactionTypeException &&
                        throwable.getMessage().contains("Invalid request type for transaction."))
                .verify();
    }

    @Test
    void updateTransaction_ShouldSuccessfullyUpdateExpense() {
        // Arrange
        io.github.cristhianm30.heikoh.application.dto.request.UpdateExpenseRequest updateExpenseRequest = io.github.cristhianm30.heikoh.application.dto.request.UpdateExpenseRequest.builder().amount(BigDecimal.TEN).build();
        TransactionResponse expectedResponse = TransactionResponse.builder().id(1L).type("Gasto").build();

        when(transactionMapper.toExpenseModel(any(io.github.cristhianm30.heikoh.application.dto.request.UpdateExpenseRequest.class))).thenReturn(expenseModel);
        when(updateTransactionServicePort.updateTransaction(anyLong(), anyLong(), eq("expense"), any(Object.class))).thenReturn(Mono.just(expenseModel));
        when(transactionMapper.toTransactionResponse(any(ExpenseModel.class))).thenReturn(expectedResponse);

        // Act & Assert
        StepVerifier.create(transactionService.updateTransaction(userId, 1L, "expense", updateExpenseRequest))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void updateTransaction_ShouldSuccessfullyUpdateIncome() {
        // Arrange
        io.github.cristhianm30.heikoh.application.dto.request.UpdateIncomeRequest updateIncomeRequest = io.github.cristhianm30.heikoh.application.dto.request.UpdateIncomeRequest.builder().amount(BigDecimal.TEN).build();
        TransactionResponse expectedResponse = TransactionResponse.builder().id(1L).type("Ingreso").build();

        when(transactionMapper.toIncomeModel(any(io.github.cristhianm30.heikoh.application.dto.request.UpdateIncomeRequest.class))).thenReturn(incomeModel);
        when(updateTransactionServicePort.updateTransaction(anyLong(), anyLong(), eq("income"), any(Object.class))).thenReturn(Mono.just(incomeModel));
        when(transactionMapper.toTransactionResponse(any(IncomeModel.class))).thenReturn(expectedResponse);

        // Act & Assert
        StepVerifier.create(transactionService.updateTransaction(userId, 1L, "income", updateIncomeRequest))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void updateTransaction_ShouldThrowInvalidTransactionTypeException() {
        // Arrange
        io.github.cristhianm30.heikoh.application.dto.request.UpdateExpenseRequest updateExpenseRequest = io.github.cristhianm30.heikoh.application.dto.request.UpdateExpenseRequest.builder().amount(BigDecimal.TEN).build();

        // Act & Assert
        StepVerifier.create(transactionService.updateTransaction(userId, 1L, "invalid", updateExpenseRequest))
                .expectErrorMatches(throwable -> throwable instanceof InvalidTransactionTypeException &&
                        throwable.getMessage().contains("Invalid request type for transaction."))
                .verify();
    }

    @Test
    void deleteTransaction_ShouldSuccessfullyDeleteExpense() {
        // Arrange
        when(deleteTransactionServicePort.deleteTransaction(anyLong(), anyLong(), eq("expense"))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(transactionService.deleteTransaction(userId, 1L, "expense"))
                .verifyComplete();
    }

    @Test
    void deleteTransaction_ShouldSuccessfullyDeleteIncome() {
        // Arrange
        when(deleteTransactionServicePort.deleteTransaction(anyLong(), anyLong(), eq("income"))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(transactionService.deleteTransaction(userId, 1L, "income"))
                .verifyComplete();
    }

    @Test
    void deleteTransaction_ShouldThrowInvalidTransactionTypeException() {
        // Arrange
        StepVerifier.create(transactionService.deleteTransaction(userId, 1L, "invalid"))
                .expectErrorMatches(throwable -> throwable instanceof InvalidTransactionTypeException &&
                        throwable.getMessage().contains("Invalid transaction type"))
                .verify();
    }
}
