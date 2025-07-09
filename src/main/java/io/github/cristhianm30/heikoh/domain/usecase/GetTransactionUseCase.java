package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.DateRangeData;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionsData;
import io.github.cristhianm30.heikoh.domain.port.in.TransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import io.github.cristhianm30.heikoh.domain.exception.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;

import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.*;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.EXPENSE_NOT_FOUND;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INCOME_NOT_FOUND;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INVALID_TRANSACTION_TYPE;

@RequiredArgsConstructor
public class GetTransactionUseCase implements TransactionServicePort {

    private final ExpenseRepositoryPort expenseRepositoryPort;
    private final IncomeRepositoryPort incomeRepositoryPort;

    @Override
    public Flux<Object> getTransactions(Long userId, TransactionsData request) {
        DateRangeData dateRange = calculateDateRange(request.getYear(), request.getMonth());
        LocalDate startDate = dateRange.getStartDate();
        LocalDate endDate = dateRange.getEndDate();

        String type = request.getType();

        Flux<Object> transactions;

        if (TYPE_INCOME.equalsIgnoreCase(type)) {
            transactions = incomeRepositoryPort.findByUserIdAndTransactionDateBetween(userId, startDate, endDate).cast(Object.class);
        } else if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            transactions = expenseRepositoryPort.findByUserIdAndTransactionDateBetween(userId, startDate, endDate).cast(Object.class);
        } else {
            Flux<ExpenseModel> expenses = expenseRepositoryPort.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
            Flux<IncomeModel> incomes = incomeRepositoryPort.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
            transactions = Flux.merge(expenses, incomes);
        }

        return transactions
                .sort(Comparator.comparing(obj -> {
                    if (obj instanceof ExpenseModel) {
                        return ((ExpenseModel) obj).getTransactionDate();
                    } else if (obj instanceof IncomeModel) {
                        return ((IncomeModel) obj).getTransactionDate();
                    }
                    return EARLIEST_DATE;
                }, Comparator.reverseOrder()))
                .skip(request.getOffset() != null ? request.getOffset() : DEFAULT_OFFSET)
                .take(request.getLimit() != null ? request.getLimit() : DEFAULT_LIMIT);
    }

    @Override
    public Mono<Object> getTransactionDetail(Long userId, Long transactionId, String type) {
        if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            return expenseRepositoryPort.findByIdAndUserId(transactionId, userId)
                    .cast(Object.class)
                    .switchIfEmpty(Mono.error(new TransactionNotFoundException(EXPENSE_NOT_FOUND + transactionId)));
        } else if (TYPE_INCOME.equalsIgnoreCase(type)) {
            return incomeRepositoryPort.findByIdAndUserId(transactionId, userId)
                    .cast(Object.class)
                    .switchIfEmpty(Mono.error(new TransactionNotFoundException(INCOME_NOT_FOUND + transactionId)));
        }
        return Mono.error(new TransactionNotFoundException(INVALID_TRANSACTION_TYPE + type));
    }

    private DateRangeData calculateDateRange(Integer year, Integer month) {
        LocalDate startDate;
        LocalDate endDate;

        if (year != null && month != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(DEFAULT_START_DAY);
            endDate = yearMonth.atEndOfMonth();
        } else if (year != null) {
            startDate = LocalDate.of(year, DEFAULT_START_MONTH, DEFAULT_START_DAY);
            endDate = LocalDate.of(year, DEFAULT_END_MONTH, DEFAULT_END_DAY);
        } else {
            startDate = EARLIEST_DATE;
            endDate = LocalDate.now();
        }
        return new DateRangeData(startDate, endDate);
    }
}

