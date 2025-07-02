package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import reactor.core.publisher.Mono;

public interface RegisterExpenseServicePort {
    Mono<ExpenseModel> registerExpense(ExpenseModel expenseToRegister);
}