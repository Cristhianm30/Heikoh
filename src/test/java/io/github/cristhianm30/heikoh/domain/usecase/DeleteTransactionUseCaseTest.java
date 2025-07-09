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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock
    private ExpenseRepositoryPort expenseRepositoryPort;

    @Mock
    private IncomeRepositoryPort incomeRepositoryPort;

    @InjectMocks
    private DeleteTransactionUseCase deleteTransactionUseCase;

    private Long userId;
    private Long transactionId;
    private ExpenseModel expenseModel;
    private IncomeModel incomeModel;

    @BeforeEach
    void setUp() {
        userId = 1L;
        transactionId = 100L;

        expenseModel = ExpenseModel.builder().id(transactionId).userId(userId).build();
        incomeModel = IncomeModel.builder().id(transactionId).userId(userId).build();
    }

    @Test
    @DisplayName("Should successfully delete an expense")
    void shouldSuccessfullyDeleteAnExpense() {
        when(expenseRepositoryPort.findByIdAndUserId(eq(transactionId), eq(userId)))
                .thenReturn(Mono.just(expenseModel));
        when(expenseRepositoryPort.deleteByIdAndUserId(eq(transactionId), eq(userId)))
                .thenReturn(Mono.empty());

        StepVerifier.create(deleteTransactionUseCase.deleteTransaction(userId, transactionId, "expense"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should successfully delete an income")
    void shouldSuccessfullyDeleteAnIncome() {
        when(incomeRepositoryPort.findByIdAndUserId(eq(transactionId), eq(userId)))
                .thenReturn(Mono.just(incomeModel));
        when(incomeRepositoryPort.deleteByIdAndUserId(eq(transactionId), eq(userId)))
                .thenReturn(Mono.empty());

        StepVerifier.create(deleteTransactionUseCase.deleteTransaction(userId, transactionId, "income"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw TransactionNotFoundException when deleting a non-existent expense")
    void shouldThrowTransactionNotFoundExceptionWhenDeletingNonExistentExpense() {
        when(expenseRepositoryPort.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Mono.empty());
        when(expenseRepositoryPort.deleteByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(deleteTransactionUseCase.deleteTransaction(userId, transactionId, "expense"))
                .expectErrorMatches(throwable -> throwable instanceof TransactionNotFoundException &&
                        throwable.getMessage().contains("Expense not found with id"))
                .verify();
    }

    @Test
    @DisplayName("Should throw TransactionNotFoundException when deleting a non-existent income")
    void shouldThrowTransactionNotFoundExceptionWhenDeletingNonExistentIncome() {
        when(incomeRepositoryPort.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Mono.empty());
        when(incomeRepositoryPort.deleteByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(deleteTransactionUseCase.deleteTransaction(userId, transactionId, "income"))
                .expectErrorMatches(throwable -> throwable instanceof TransactionNotFoundException &&
                        throwable.getMessage().contains("Income not found with id"))
                .verify();
    }

    @Test
    @DisplayName("Should throw InvalidTransactionTypeException for invalid transaction type")
    void shouldThrowInvalidTransactionTypeExceptionForInvalidTransactionType() {
        StepVerifier.create(deleteTransactionUseCase.deleteTransaction(userId, transactionId, "invalid"))
                .expectErrorMatches(throwable -> throwable instanceof InvalidTransactionTypeException &&
                        throwable.getMessage().contains("Invalid transaction type"))
                .verify();
    }
}
