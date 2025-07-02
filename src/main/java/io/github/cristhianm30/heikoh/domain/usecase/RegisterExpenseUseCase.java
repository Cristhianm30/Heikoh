package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterExpenseServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RegisterExpenseUseCase implements RegisterExpenseServicePort {

    private final ExpenseRepositoryPort expenseRepository;

    @Override
    public Mono<ExpenseModel> registerExpense(ExpenseModel expenseToRegister) {
        expenseToRegister.setCreatedAt(LocalDateTime.now());
        expenseToRegister.setUpdatedAt(LocalDateTime.now());
        return expenseRepository.save(expenseToRegister);
    }
}
