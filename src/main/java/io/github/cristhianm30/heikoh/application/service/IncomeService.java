package io.github.cristhianm30.heikoh.application.service;


import io.github.cristhianm30.heikoh.application.dto.request.IncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.response.IncomeResponse;
import reactor.core.publisher.Mono;

public interface IncomeService {
    Mono<IncomeResponse> registerIncome(IncomeRequest request, Long userId);
}
