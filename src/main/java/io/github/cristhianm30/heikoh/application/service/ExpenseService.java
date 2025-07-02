package io.github.cristhianm30.heikoh.application.service;


import io.github.cristhianm30.heikoh.application.dto.request.ExpenseRequest;
import io.github.cristhianm30.heikoh.application.dto.response.ExpenseResponse;
import reactor.core.publisher.Mono;

public interface ExpenseService {
    Mono<ExpenseResponse> registerExpense(ExpenseRequest request, Long userId);
}
