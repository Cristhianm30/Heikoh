package io.github.cristhianm30.heikoh.infrastructure.persistence.repository;

import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExpenseRepository extends ReactiveCrudRepository<ExpenseEntity, Long> {

    Flux<ExpenseEntity> findByUserId(Long userId);

    Flux<ExpenseEntity> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate")
    Mono<BigDecimal> sumAmountByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Mono<ExpenseEntity> findByIdAndUserId(Long id, Long userId);

    Mono<Void> deleteByIdAndUserId(Long id, Long userId);

}
