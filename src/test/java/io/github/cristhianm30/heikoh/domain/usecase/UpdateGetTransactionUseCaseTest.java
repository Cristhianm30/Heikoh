package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;
import io.github.cristhianm30.heikoh.domain.exception.TransactionNotFoundException;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateGetTransactionUseCaseTest {

    @Mock
    private ExpenseRepositoryPort expenseRepositoryPort;

    @Mock
    private IncomeRepositoryPort incomeRepositoryPort;

    @InjectMocks
    private UpdateTransactionUseCase updateTransactionUseCase;

    private Long userId;
    private Long transactionId;
    private ExpenseModel expenseModel;
    private IncomeModel incomeModel;

    @BeforeEach
    void setUp() {
        userId = 1L;
        transactionId = 100L;

        expenseModel = ExpenseModel.builder()
                .id(transactionId)
                .userId(userId)
                .amount(BigDecimal.valueOf(50.00))
                .description("Old Expense")
                .transactionDate(LocalDate.now())
                .category("Food")
                .paymentMethod("Cash")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        incomeModel = IncomeModel.builder()
                .id(transactionId)
                .userId(userId)
                .amount(BigDecimal.valueOf(100.00))
                .description("Old Income")
                .transactionDate(LocalDate.now())
                .origin("Salary")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("Should successfully update an expense")
    void shouldSuccessfullyUpdateAnExpense() {
        ExpenseModel updatedExpenseData = ExpenseModel.builder()
                .amount(BigDecimal.valueOf(75.00))
                .description("New Expense")
                .transactionDate(LocalDate.now().plusDays(1))
                .category("Transport")
                .paymentMethod("Card")
                .build();

        ExpenseModel savedExpense = ExpenseModel.builder()
                .id(transactionId)
                .userId(userId)
                .amount(updatedExpenseData.getAmount())
                .description(updatedExpenseData.getDescription())
                .transactionDate(updatedExpenseData.getTransactionDate())
                .category(updatedExpenseData.getCategory())
                .paymentMethod(updatedExpenseData.getPaymentMethod())
                .createdAt(expenseModel.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(expenseRepositoryPort.findByIdAndUserId(eq(transactionId), eq(userId)))
                .thenReturn(Mono.just(expenseModel));
        when(expenseRepositoryPort.save(any(ExpenseModel.class)))
                .thenReturn(Mono.just(savedExpense));

        StepVerifier.create(updateTransactionUseCase.updateTransaction(userId, transactionId, "expense", updatedExpenseData))
                .expectNextMatches(result -> {
                    ExpenseModel res = (ExpenseModel) result;
                    return res.getId().equals(transactionId) &&
                            res.getAmount().equals(updatedExpenseData.getAmount()) &&
                            res.getDescription().equals(updatedExpenseData.getDescription());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should successfully update an income")
    void shouldSuccessfullyUpdateAnIncome() {
        IncomeModel updatedIncomeData = IncomeModel.builder()
                .amount(BigDecimal.valueOf(120.00))
                .description("New Income")
                .transactionDate(LocalDate.now().plusDays(1))
                .origin("Freelance")
                .build();

        IncomeModel savedIncome = IncomeModel.builder()
                .id(transactionId)
                .userId(userId)
                .amount(updatedIncomeData.getAmount())
                .description(updatedIncomeData.getDescription())
                .transactionDate(updatedIncomeData.getTransactionDate())
                .origin(updatedIncomeData.getOrigin())
                .createdAt(incomeModel.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(incomeRepositoryPort.findByIdAndUserId(eq(transactionId), eq(userId)))
                .thenReturn(Mono.just(incomeModel));
        when(incomeRepositoryPort.save(any(IncomeModel.class)))
                .thenReturn(Mono.just(savedIncome));

        StepVerifier.create(updateTransactionUseCase.updateTransaction(userId, transactionId, "income", updatedIncomeData))
                .expectNextMatches(result -> {
                    IncomeModel res = (IncomeModel) result;
                    return res.getId().equals(transactionId) &&
                            res.getAmount().equals(updatedIncomeData.getAmount()) &&
                            res.getDescription().equals(updatedIncomeData.getDescription());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw TransactionNotFoundException when updating a non-existent expense")
    void shouldThrowTransactionNotFoundExceptionWhenUpdatingNonExistentExpense() {
        ExpenseModel updatedExpenseData = ExpenseModel.builder().build();

        when(expenseRepositoryPort.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(updateTransactionUseCase.updateTransaction(userId, transactionId, "expense", updatedExpenseData))
                .expectErrorMatches(throwable -> throwable instanceof TransactionNotFoundException &&
                        throwable.getMessage().contains("Expense not found with id"))
                .verify();
    }

    @Test
    @DisplayName("Should throw TransactionNotFoundException when updating a non-existent income")
    void shouldThrowTransactionNotFoundExceptionWhenUpdatingNonExistentIncome() {
        IncomeModel updatedIncomeData = IncomeModel.builder().build();

        when(incomeRepositoryPort.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(updateTransactionUseCase.updateTransaction(userId, transactionId, "income", updatedIncomeData))
                .expectErrorMatches(throwable -> throwable instanceof TransactionNotFoundException &&
                        throwable.getMessage().contains("Income not found with id"))
                .verify();
    }

    @Test
    @DisplayName("Should throw InvalidTransactionTypeException for invalid transaction type")
    void shouldThrowInvalidTransactionTypeExceptionForInvalidTransactionType() {
        StepVerifier.create(updateTransactionUseCase.updateTransaction(userId, transactionId, "invalid", expenseModel))
                .expectErrorMatches(throwable -> throwable instanceof InvalidTransactionTypeException &&
                        throwable.getMessage().contains("Invalid transaction type"))
                .verify();
    }
}
