package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.InvalidGroupByException;
import io.github.cristhianm30.heikoh.domain.model.AggregationData;
import io.github.cristhianm30.heikoh.domain.port.in.GetExpenseAggregationServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants;
import io.github.cristhianm30.heikoh.domain.util.constant.QueryConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
public class GetExpenseAggregationUseCase implements GetExpenseAggregationServicePort {

    private final ExpenseRepositoryPort expenseRepositoryPort;

    @Override
    public Flux<AggregationData> getExpenseAggregation(Long userId, LocalDate startDate, LocalDate endDate, String groupBy) {
        if (QueryConstant.CATEGORY_FIELD.equalsIgnoreCase(groupBy)) {
            if (startDate != null && endDate != null) {
                return expenseRepositoryPort.sumAmountByUserIdAndDateBetweenByCategory(userId, startDate, endDate);
            } else {
                return expenseRepositoryPort.sumAmountByUserIdByCategory(userId);
            }
        } else if (QueryConstant.PAYMENT_METHOD_FIELD.equalsIgnoreCase(groupBy)) {
            if (startDate != null && endDate != null) {
                return expenseRepositoryPort.sumAmountByUserIdAndDateBetweenByPaymentMethod(userId, startDate, endDate);
            } else {
                return expenseRepositoryPort.sumAmountByUserIdByPaymentMethod(userId);
            }
        } else {
            return Flux.error(new InvalidGroupByException(ExceptionConstants.INVALID_GROUP_BY_PARAMETER));
        }
    }
}
