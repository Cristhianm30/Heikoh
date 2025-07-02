package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterExpenseUseCaseTest {

    @Mock
    private ExpenseRepositoryPort expenseRepositoryPort;

    @InjectMocks
    private RegisterExpenseUseCase registerExpenseUseCase;

    private ExpenseModel expenseModel;

    @BeforeEach
    void setUp() {
        expenseModel = ExpenseModel.builder()
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now())
                .category("Food")
                .paymentMethod("Credit Card")
                .userId(1L)
                .build();
    }

    @Test
    void registerExpense_ShouldReturnExpenseModel_WhenSuccessful() {
        when(expenseRepositoryPort.save(any(ExpenseModel.class))).thenReturn(Mono.just(expenseModel));

        StepVerifier.create(registerExpenseUseCase.registerExpense(expenseModel))
                .expectNext(expenseModel)
                .verifyComplete();
    }

    @Test
    void registerExpense_ShouldReturnError_WhenPersistenceFails() {
        when(expenseRepositoryPort.save(any(ExpenseModel.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));

        StepVerifier.create(registerExpenseUseCase.registerExpense(expenseModel))
                .expectError(RuntimeException.class)
                .verify();
    }
}
