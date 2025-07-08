package io.github.cristhianm30.heikoh.infrastructure.persistence.repository;

import io.github.cristhianm30.heikoh.infrastructure.entity.IncomeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IncomeRepository extends ReactiveCrudRepository<IncomeEntity, Long> {

    Flux<IncomeEntity> findByUserId(Long userId);

    Flux<IncomeEntity> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(amount) FROM incomes WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate")
    Mono<BigDecimal> sumAmountByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Mono<IncomeEntity> findByIdAndUserId(Long id, Long userId);

}