package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGetTransactionUseCaseTest {

    @Mock
    private ExpenseRepositoryPort expenseRepositoryPort;

    @Mock
    private IncomeRepositoryPort incomeRepositoryPort;

    @InjectMocks
    private CreateTransactionUseCase createTransactionUseCase;

    private Long userId;
    private ExpenseModel expenseModel;
    private IncomeModel incomeModel;

    @BeforeEach
    void setUp() {
        userId = 1L;

        expenseModel = ExpenseModel.builder()
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .build();

        incomeModel = IncomeModel.builder()
                .amount(BigDecimal.valueOf(200.00))
                .description("Freelance Payment")
                .build();
    }

    @Test
    @DisplayName("Should successfully register an expense")
    void shouldSuccessfullyRegisterAnExpense() {
        when(expenseRepositoryPort.save(any(ExpenseModel.class)))
                .thenReturn(Mono.just(expenseModel));

        StepVerifier.create(createTransactionUseCase.registerTransaction(userId, "expense", expenseModel))
                .expectNextMatches(result -> {
                    ExpenseModel savedExpense = (ExpenseModel) result;
                    assertNotNull(savedExpense.getUserId());
                    assertNotNull(savedExpense.getCreatedAt());
                    assertNotNull(savedExpense.getUpdatedAt());
                    assertEquals(userId, savedExpense.getUserId());
                    return true;
                })
                .verifyComplete();

        ArgumentCaptor<ExpenseModel> expenseCaptor = ArgumentCaptor.forClass(ExpenseModel.class);
        verify(expenseRepositoryPort).save(expenseCaptor.capture());
        ExpenseModel capturedExpense = expenseCaptor.getValue();
        assertEquals(userId, capturedExpense.getUserId());
        assertNotNull(capturedExpense.getCreatedAt());
        assertNotNull(capturedExpense.getUpdatedAt());
    }

    @Test
    @DisplayName("Should successfully register an income")
    void shouldSuccessfullyRegisterAnIncome() {
        when(incomeRepositoryPort.save(any(IncomeModel.class)))
                .thenReturn(Mono.just(incomeModel));

        StepVerifier.create(createTransactionUseCase.registerTransaction(userId, "income", incomeModel))
                .expectNextMatches(result -> {
                    IncomeModel savedIncome = (IncomeModel) result;
                    assertNotNull(savedIncome.getUserId());
                    assertNotNull(savedIncome.getCreatedAt());
                    assertNotNull(savedIncome.getUpdatedAt());
                    assertEquals(userId, savedIncome.getUserId());
                    return true;
                })
                .verifyComplete();

        ArgumentCaptor<IncomeModel> incomeCaptor = ArgumentCaptor.forClass(IncomeModel.class);
        verify(incomeRepositoryPort).save(incomeCaptor.capture());
        IncomeModel capturedIncome = incomeCaptor.getValue();
        assertEquals(userId, capturedIncome.getUserId());
        assertNotNull(capturedIncome.getCreatedAt());
        assertNotNull(capturedIncome.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw InvalidTransactionTypeException for invalid transaction type")
    void shouldThrowInvalidTransactionTypeExceptionForInvalidTransactionType() {
        StepVerifier.create(createTransactionUseCase.registerTransaction(userId, "invalid", expenseModel))
                .expectErrorMatches(throwable -> throwable instanceof InvalidTransactionTypeException &&
                        throwable.getMessage().contains("Invalid transaction type"))
                .verify();
    }
}
