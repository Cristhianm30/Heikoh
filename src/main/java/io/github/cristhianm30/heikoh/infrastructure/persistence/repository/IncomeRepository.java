package io.github.cristhianm30.heikoh.infrastructure.persistence.repository;

import io.github.cristhianm30.heikoh.infrastructure.entity.IncomeEntity;
import io.github.cristhianm30.heikoh.infrastructure.entity.AggregationResult;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.github.cristhianm30.heikoh.domain.util.constant.QueryConstant.*;

public interface IncomeRepository extends ReactiveCrudRepository<IncomeEntity, Long> {

    Flux<IncomeEntity> findByUserId(Long userId);

    Flux<IncomeEntity> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(SELECT_SUM_AMOUNT_FROM_INCOMES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN)
    Mono<BigDecimal> sumAmountByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(SELECT_SUM_AMOUNT_FROM_INCOMES_WHERE_USER_ID)
    Mono<BigDecimal> sumAmountByUserId(Long userId);

    Mono<IncomeEntity> findByIdAndUserId(Long id, Long userId);

    Mono<Void> deleteByIdAndUserId(Long id, Long userId);

    @Query("SELECT origin AS `key`, SUM(amount) AS total_amount FROM incomes WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate GROUP BY origin")
    Flux<AggregationResult> sumAmountByUserIdAndTransactionDateBetweenByOrigin(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT origin AS `key`, SUM(amount) AS total_amount FROM incomes WHERE user_id = :userId GROUP BY origin")
    Flux<AggregationResult> sumAmountByUserIdByOrigin(Long userId);
}