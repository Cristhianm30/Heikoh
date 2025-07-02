package io.github.cristhianm30.heikoh.infrastructure.persistence.repository;

import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExpenseRepository extends ReactiveCrudRepository<ExpenseEntity, Long> {

    Flux<ExpenseEntity> findByUserId(Long userId);

    Flux<ExpenseEntity> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Mono<BigDecimal> sumAmountByUserId(Long userId);

    Mono<BigDecimal> sumAmountByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}