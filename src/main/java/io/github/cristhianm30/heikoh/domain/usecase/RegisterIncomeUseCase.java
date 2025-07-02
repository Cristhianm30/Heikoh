package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterIncomeServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RegisterIncomeUseCase implements RegisterIncomeServicePort {

    private final IncomeRepositoryPort incomeRepository;

    @Override
    public Mono<IncomeModel> registerIncome(IncomeModel incomeToRegister) {
        incomeToRegister.setCreatedAt(LocalDateTime.now());
        incomeToRegister.setUpdatedAt(LocalDateTime.now());
        return incomeRepository.save(incomeToRegister);
    }
}
