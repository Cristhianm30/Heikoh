package io.github.cristhianm30.heikoh.application.service;


import io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest;
import io.github.cristhianm30.heikoh.application.dto.response.ExpenseResponse;
import reactor.core.publisher.Mono;

public interface ExpenseService {
    Mono<ExpenseResponse> registerExpense(RegisterExpenseRequest request, Long userId);
}
