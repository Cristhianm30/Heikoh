package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.ExpenseRequest;
import io.github.cristhianm30.heikoh.application.dto.response.ExpenseResponse;
import io.github.cristhianm30.heikoh.application.mapper.ExpenseMapper;
import io.github.cristhianm30.heikoh.domain.exception.ExpenseNotRegisteredException;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterExpenseServicePort;
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

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.EXPENSE_NOT_REGISTERED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private RegisterExpenseServicePort registerExpenseServicePort;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private ExpenseRequest expenseRequest;
    private ExpenseModel expenseModel;
    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequest(
                BigDecimal.valueOf(50.00),
                "Groceries",
                LocalDate.now(),
                "Food",
                "Credit Card"
        );

        expenseModel = ExpenseModel.builder()
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now())
                .category("Food")
                .paymentMethod("Credit Card")
                .userId(1L)
                .build();

        expenseResponse = new ExpenseResponse(
                1L,
                1L,
                BigDecimal.valueOf(50.00),
                "Groceries",
                LocalDate.now(),
                "Food",
                "Credit Card",
                null,
                null
        );
    }

    @Test
    void registerExpense_ShouldReturnExpenseResponse_WhenSuccessful() {
        when(expenseMapper.toModel(any(ExpenseRequest.class))).thenReturn(expenseModel);
        when(registerExpenseServicePort.registerExpense(any(ExpenseModel.class))).thenReturn(Mono.just(expenseModel));
        when(expenseMapper.toResponse(any(ExpenseModel.class))).thenReturn(expenseResponse);

        StepVerifier.create(expenseService.registerExpense(expenseRequest, 1L))
                .expectNextMatches(response -> response.getId().equals(1L) && response.getAmount().equals(BigDecimal.valueOf(50.00)))
                .verifyComplete();
    }

    @Test
    void registerExpense_ShouldThrowExpenseNotRegisteredException_WhenRegistrationFails() {
        when(expenseMapper.toModel(any(ExpenseRequest.class))).thenReturn(expenseModel);
        when(registerExpenseServicePort.registerExpense(any(ExpenseModel.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));

        StepVerifier.create(expenseService.registerExpense(expenseRequest, 1L))
                .expectErrorMatches(throwable ->
                        throwable instanceof ExpenseNotRegisteredException &&
                                EXPENSE_NOT_REGISTERED.equals(throwable.getMessage()))
                .verify();
    }
}
