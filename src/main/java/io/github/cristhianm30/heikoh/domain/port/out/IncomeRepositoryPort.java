package io.github.cristhianm30.heikoh.domain.port.out;

import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IncomeRepositoryPort {
    Mono<IncomeModel> save(IncomeModel income);
    Mono<IncomeModel> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<BigDecimal> sumAmountByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    Flux<IncomeModel> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    Mono<IncomeModel> findByIdAndUserId(Long id, Long userId);
    Mono<Void> deleteByIdAndUserId(Long id, Long userId);
}
