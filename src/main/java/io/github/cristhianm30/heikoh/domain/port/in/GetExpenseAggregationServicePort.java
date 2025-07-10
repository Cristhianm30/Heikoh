package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.ExpenseAggregationData;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface GetExpenseAggregationServicePort {
    Flux<ExpenseAggregationData> getExpenseAggregation(Long userId, LocalDate startDate, LocalDate endDate, String groupBy);
}
