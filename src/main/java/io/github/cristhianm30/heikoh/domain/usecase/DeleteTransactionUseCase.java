package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.TransactionNotFoundException;
import io.github.cristhianm30.heikoh.domain.port.in.DeleteTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.*;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_EXPENSE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_INCOME;
import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;

@RequiredArgsConstructor
public class DeleteTransactionUseCase implements DeleteTransactionServicePort {

    private final ExpenseRepositoryPort expenseRepositoryPort;
    private final IncomeRepositoryPort incomeRepositoryPort;

    @Override
    public Mono<Void> deleteTransaction(Long userId, Long transactionId, String type) {
        if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            return expenseRepositoryPort.findByIdAndUserId(transactionId, userId)
                    .switchIfEmpty(Mono.error(new TransactionNotFoundException(EXPENSE_NOT_FOUND + transactionId)))
                    .then(expenseRepositoryPort.deleteByIdAndUserId(transactionId, userId));
        } else if (TYPE_INCOME.equalsIgnoreCase(type)) {
            return incomeRepositoryPort.findByIdAndUserId(transactionId, userId)
                    .switchIfEmpty(Mono.error(new TransactionNotFoundException(INCOME_NOT_FOUND + transactionId)))
                    .then(incomeRepositoryPort.deleteByIdAndUserId(transactionId, userId));
        }
        return Mono.error(new InvalidTransactionTypeException(INVALID_TRANSACTION_TYPE + type));
    }
}