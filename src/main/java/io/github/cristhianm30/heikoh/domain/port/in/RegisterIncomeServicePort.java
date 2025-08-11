package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import reactor.core.publisher.Mono;

public interface RegisterIncomeServicePort {
    Mono<IncomeModel> registerIncome(IncomeModel incomeToRegister);
}
