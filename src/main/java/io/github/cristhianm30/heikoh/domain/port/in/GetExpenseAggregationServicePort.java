package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface GetExpenseAggregationServicePort {
    Flux<AggregationData> getExpenseAggregation(Long userId, LocalDate startDate, LocalDate endDate, String groupBy);
}
