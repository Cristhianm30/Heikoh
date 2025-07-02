package io.github.cristhianm30.heikoh.domain.port.out;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExpenseRepositoryPort {
    Mono<ExpenseModel> save(ExpenseModel expense);
    Mono<ExpenseModel> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<BigDecimal> sumAmountByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}