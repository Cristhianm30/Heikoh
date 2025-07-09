package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INVALID_TRANSACTION_TYPE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_EXPENSE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_INCOME;
import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;

@RequiredArgsConstructor
public class CreateTransactionUseCase implements RegisterTransactionServicePort {

    private final ExpenseRepositoryPort expenseRepositoryPort;
    private final IncomeRepositoryPort incomeRepositoryPort;

    private void setCommonTransactionFields(Long userId, Object transactionData) {
        LocalDateTime now = LocalDateTime.now();
        if (transactionData instanceof ExpenseModel expense) {
            expense.setUserId(userId);
            expense.setCreatedAt(now);
            expense.setUpdatedAt(now);
        } else if (transactionData instanceof IncomeModel income) {
            income.setUserId(userId);
            income.setCreatedAt(now);
            income.setUpdatedAt(now);
        }
    }

    @Override
    public Mono<Object> registerTransaction(Long userId, String type, Object transactionData) {
        setCommonTransactionFields(userId, transactionData);

        if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            ExpenseModel expense = (ExpenseModel) transactionData;
            return expenseRepositoryPort.save(expense).cast(Object.class);

        } else if (TYPE_INCOME.equalsIgnoreCase(type)) {
            IncomeModel income = (IncomeModel) transactionData;
            return incomeRepositoryPort.save(income).cast(Object.class);
        }

        return Mono.error(new InvalidTransactionTypeException(INVALID_TRANSACTION_TYPE + type));
    }
}