package io.github.cristhianm30.heikoh.domain.port.out;

import io.github.cristhianm30.heikoh.domain.model.ExpenseAggregationData;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExpenseRepositoryPort {
    Mono<ExpenseModel> save(ExpenseModel expense);

    Mono<ExpenseModel> findById(Long id);

    Mono<Void> deleteById(Long id);

    Mono<BigDecimal> sumAmountByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Mono<BigDecimal> sumAmountByUserId(Long userId);

    Flux<ExpenseModel> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Mono<ExpenseModel> findByIdAndUserId(Long id, Long userId);

    Mono<Void> deleteByIdAndUserId(Long id, Long userId);

    Flux<ExpenseAggregationData> sumAmountByUserIdAndDateBetweenByCategory(Long userId, LocalDate startDate, LocalDate endDate);

    Flux<ExpenseAggregationData> sumAmountByUserIdByCategory(Long userId);

    Flux<ExpenseAggregationData> sumAmountByUserIdAndDateBetweenByPaymentMethod(Long userId, LocalDate startDate, LocalDate endDate);

    Flux<ExpenseAggregationData> sumAmountByUserIdByPaymentMethod(Long userId);
}