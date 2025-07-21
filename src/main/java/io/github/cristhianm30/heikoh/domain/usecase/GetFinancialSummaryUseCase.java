package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.FinancialSummaryData;
import io.github.cristhianm30.heikoh.domain.port.in.GetFinancialSummaryServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.EARLIEST_DATE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.LASTEST_DATE;

@RequiredArgsConstructor
public class GetFinancialSummaryUseCase implements GetFinancialSummaryServicePort {

    private final ExpenseRepositoryPort expenseRepositoryPort;
    private final IncomeRepositoryPort incomeRepositoryPort;

    @Override
    public Mono<FinancialSummaryData> getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        Mono<BigDecimal> totalIncomeMono;
        Mono<BigDecimal> totalExpenseMono;

        if (startDate != null && endDate != null) {
            totalIncomeMono = incomeRepositoryPort.sumAmountByUserIdAndDateBetween(userId, startDate, endDate);
            totalExpenseMono = expenseRepositoryPort.sumAmountByUserIdAndDateBetween(userId, startDate, endDate);
        }else if (startDate != null) {
            LocalDate lastestDate = LASTEST_DATE;
            totalIncomeMono = incomeRepositoryPort.sumAmountByUserIdAndDateBetween(userId, startDate, lastestDate);
            totalExpenseMono = expenseRepositoryPort.sumAmountByUserIdAndDateBetween(userId, startDate, lastestDate);

        }else if (endDate != null) {
            LocalDate earliestDate= EARLIEST_DATE;
            totalIncomeMono = incomeRepositoryPort.sumAmountByUserIdAndDateBetween(userId, earliestDate, endDate);
            totalExpenseMono = expenseRepositoryPort.sumAmountByUserIdAndDateBetween(userId, earliestDate, endDate);

        } else {
            totalIncomeMono = incomeRepositoryPort.sumAmountByUserId(userId);
            totalExpenseMono = expenseRepositoryPort.sumAmountByUserId(userId);
        }

        return Mono.zip(totalIncomeMono, totalExpenseMono)
                .map(tuple -> {
                    BigDecimal totalIncome = tuple.getT1();
                    BigDecimal totalExpense = tuple.getT2();
                    BigDecimal totalBalance = totalIncome.subtract(totalExpense);
                    return new FinancialSummaryData(totalIncome, totalExpense, totalBalance);
                });
    }
}
