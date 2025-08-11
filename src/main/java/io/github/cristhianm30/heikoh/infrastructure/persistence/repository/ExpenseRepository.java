package io.github.cristhianm30.heikoh.infrastructure.persistence.repository;

import io.github.cristhianm30.heikoh.infrastructure.entity.AggregationResult;
import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.github.cristhianm30.heikoh.domain.util.constant.QueryConstant.*;

public interface ExpenseRepository extends ReactiveCrudRepository<ExpenseEntity, Long> {

    Flux<ExpenseEntity> findByUserId(Long userId);

    Flux<ExpenseEntity> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(SELECT_SUM_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN)
    Mono<BigDecimal> sumAmountByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(SELECT_SUM_AMOUNT_FROM_EXPENSES_WHERE_USER_ID)
    Mono<BigDecimal> sumAmountByUserId(Long userId);

    Mono<ExpenseEntity> findByIdAndUserId(Long id, Long userId);

    Mono<Void> deleteByIdAndUserId(Long id, Long userId);

    @Query(SELECT_CATEGORY_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN_GROUP_BY_CATEGORY)
    Flux<AggregationResult> sumAmountByUserIdAndTransactionDateBetweenByCategory(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(SELECT_CATEGORY_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_GROUP_BY_CATEGORY)
    Flux<AggregationResult> sumAmountByUserIdByCategory(Long userId);

    @Query(SELECT_PAYMENT_METHOD_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN_GROUP_BY_PAYMENT_METHOD)
    Flux<AggregationResult> sumAmountByUserIdAndTransactionDateBetweenByPaymentMethod(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(SELECT_PAYMENT_METHOD_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_GROUP_BY_PAYMENT_METHOD)
    Flux<AggregationResult> sumAmountByUserIdByPaymentMethod(Long userId);
}
