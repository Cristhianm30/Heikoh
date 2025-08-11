package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import io.github.cristhianm30.heikoh.infrastructure.entity.IncomeEntity;
import io.github.cristhianm30.heikoh.infrastructure.entity.AggregationResult;
import io.github.cristhianm30.heikoh.infrastructure.mapper.IncomeEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
@Transactional
@RequiredArgsConstructor
public class IncomePersistenceAdapter implements IncomeRepositoryPort {

    private final IncomeRepository incomeRepository;
    private final IncomeEntityMapper incomeEntityMapper;

    @Override
    public Mono<IncomeModel> save(IncomeModel income) {
        IncomeEntity entity = incomeEntityMapper.toEntity(income);
        return incomeRepository.save(entity)
                .map(incomeEntityMapper::toModel);
    }

    @Override
    public Mono<IncomeModel> findById(Long id) {
        return incomeRepository.findById(id)
                .map(incomeEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return incomeRepository.deleteById(id);
    }

    @Override
    public Mono<BigDecimal> sumAmountByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.sumAmountByUserIdAndTransactionDateBetween(userId, startDate, endDate)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Override
    public Mono<BigDecimal> sumAmountByUserId(Long userId) {
        return incomeRepository.sumAmountByUserId(userId)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Override
    public Flux<IncomeModel> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate)
                .map(incomeEntityMapper::toModel);
    }

    @Override
    public Mono<IncomeModel> findByIdAndUserId(Long id, Long userId) {
        return incomeRepository.findByIdAndUserId(id, userId)
                .map(incomeEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteByIdAndUserId(Long id, Long userId) {
        return incomeRepository.deleteByIdAndUserId(id,userId);
    }

    @Override
    public Flux<AggregationData> sumAmountByUserIdAndDateBetweenByOrigin(Long userId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.sumAmountByUserIdAndTransactionDateBetweenByOrigin(userId, startDate, endDate)
                .map(this::mapToAggregationData);
    }

    @Override
    public Flux<AggregationData> sumAmountByUserIdByOrigin(Long userId) {
        return incomeRepository.sumAmountByUserIdByOrigin(userId)
                .map(this::mapToAggregationData);
    }

    private AggregationData mapToAggregationData(AggregationResult result) {
        return new AggregationData(result.getKey(), result.getTotalAmount() != null ? result.getTotalAmount() : BigDecimal.ZERO);
    }
}
