package io.github.cristhianm30.heikoh.application.service;


import io.github.cristhianm30.heikoh.application.dto.request.RegisterIncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.response.IncomeResponse;
import reactor.core.publisher.Mono;

public interface IncomeService {
    Mono<IncomeResponse> registerIncome(RegisterIncomeRequest request, Long userId);
}
