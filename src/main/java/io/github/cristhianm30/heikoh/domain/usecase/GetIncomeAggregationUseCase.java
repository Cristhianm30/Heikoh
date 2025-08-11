package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.port.in.GetIncomeAggregationServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RequiredArgsConstructor
public class GetIncomeAggregationUseCase implements GetIncomeAggregationServicePort {

    private final IncomeRepositoryPort incomeRepositoryPort;

    @Override
    public Flux<AggregationData> getIncomeAggregation(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return incomeRepositoryPort.sumAmountByUserIdAndDateBetweenByOrigin(userId, startDate, endDate);
        } else {
            return incomeRepositoryPort.sumAmountByUserIdByOrigin(userId);
        }
    }
}
