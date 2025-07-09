package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.TransactionNotFoundException;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.in.UpdateTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.*;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_EXPENSE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_INCOME;
import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;

@RequiredArgsConstructor
public class UpdateTransactionUseCase implements UpdateTransactionServicePort {

    private final ExpenseRepositoryPort expenseRepositoryPort;
    private final IncomeRepositoryPort incomeRepositoryPort;

    @Override
    public Mono<Object> updateTransaction(Long userId, Long transactionId, String type, Object transactionData) {
        if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            return updateExpense(userId, transactionId, (ExpenseModel) transactionData);
        } else if (TYPE_INCOME.equalsIgnoreCase(type)) {
            return updateIncome(userId, transactionId, (IncomeModel) transactionData);
        }
        return Mono.error(new InvalidTransactionTypeException(INVALID_TRANSACTION_TYPE + type));
    }

    private Mono<Object> updateExpense(Long userId, Long transactionId, ExpenseModel updateData) {
        return expenseRepositoryPort.findByIdAndUserId(transactionId, userId)
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(EXPENSE_NOT_FOUND + transactionId)))
                .flatMap(existingExpense -> {
                    existingExpense.setAmount(updateData.getAmount());
                    existingExpense.setDescription(updateData.getDescription());
                    existingExpense.setTransactionDate(updateData.getTransactionDate());
                    existingExpense.setCategory(updateData.getCategory());
                    existingExpense.setPaymentMethod(updateData.getPaymentMethod());
                    existingExpense.setUpdatedAt(LocalDateTime.now());
                    return expenseRepositoryPort.save(existingExpense);
                })
                .cast(Object.class);
    }

    private Mono<Object> updateIncome(Long userId, Long transactionId, IncomeModel updateData) {
        return incomeRepositoryPort.findByIdAndUserId(transactionId, userId)
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(INCOME_NOT_FOUND + transactionId)))
                .flatMap(existingIncome -> {
                    existingIncome.setAmount(updateData.getAmount());
                    existingIncome.setDescription(updateData.getDescription());
                    existingIncome.setTransactionDate(updateData.getTransactionDate());
                    existingIncome.setOrigin(updateData.getOrigin());
                    existingIncome.setUpdatedAt(LocalDateTime.now());
                    return incomeRepositoryPort.save(existingIncome);
                })
                .cast(Object.class);
    }
}