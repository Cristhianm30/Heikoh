package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import io.github.cristhianm30.heikoh.infrastructure.mapper.ExpenseEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
@Transactional
@RequiredArgsConstructor
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
        return expenseRepository.sumAmountByUserIdAndTransactionDateBetween(userId, startDate, endDate);
    }
}