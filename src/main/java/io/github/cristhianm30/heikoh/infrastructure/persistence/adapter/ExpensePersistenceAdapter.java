package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.ExpenseAggregationData;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseAggregationResult;
import io.github.cristhianm30.heikoh.infrastructure.mapper.ExpenseEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExpensePersistenceAdapter implements ExpenseRepositoryPort {

    private final ExpenseRepository expenseRepository;
    private final ExpenseEntityMapper expenseEntityMapper;

    @Override
    public Mono<ExpenseModel> save(ExpenseModel expense) {
        ExpenseEntity entity = expenseEntityMapper.toEntity(expense);
        return expenseRepository.save(entity)
                .map(expenseEntityMapper::toModel);
    }

    @Override
    public Mono<ExpenseModel> findById(Long id) {
        return expenseRepository.findById(id)
                .map(expenseEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return expenseRepository.deleteById(id);
    }

    @Override
    public Mono<BigDecimal> sumAmountByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.sumAmountByUserIdAndTransactionDateBetween(userId, startDate, endDate)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Override
    public Mono<BigDecimal> sumAmountByUserId(Long userId) {
        return expenseRepository.sumAmountByUserId(userId)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Override
    public Flux<ExpenseModel> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate)
                .map(expenseEntityMapper::toModel);
    }

    @Override
    public Mono<ExpenseModel> findByIdAndUserId(Long id, Long userId) {
        return expenseRepository.findByIdAndUserId(id, userId)
                .map(expenseEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteByIdAndUserId(Long id, Long userId) {
        return expenseRepository.deleteByIdAndUserId(id, userId);
    }

    @Override
    public Flux<ExpenseAggregationData> sumAmountByUserIdAndDateBetweenByCategory(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.sumAmountByUserIdAndTransactionDateBetweenByCategory(userId, startDate, endDate)
                .map(this::mapToExpenseAggregationData);
    }

    @Override
    public Flux<ExpenseAggregationData> sumAmountByUserIdByCategory(Long userId) {
        return expenseRepository.sumAmountByUserIdByCategory(userId)
                .map(this::mapToExpenseAggregationData);
    }

    @Override
    public Flux<ExpenseAggregationData> sumAmountByUserIdAndDateBetweenByPaymentMethod(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.sumAmountByUserIdAndTransactionDateBetweenByPaymentMethod(userId, startDate, endDate)
                .map(this::mapToExpenseAggregationData);
    }

    @Override
    public Flux<ExpenseAggregationData> sumAmountByUserIdByPaymentMethod(Long userId) {
        return expenseRepository.sumAmountByUserIdByPaymentMethod(userId)
                .map(this::mapToExpenseAggregationData);
    }

    private ExpenseAggregationData mapToExpenseAggregationData(ExpenseAggregationResult result) {
        return new ExpenseAggregationData(result.getKey(), result.getTotalAmount() != null ? result.getTotalAmount() : BigDecimal.ZERO);
    }
}
