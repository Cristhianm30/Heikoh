package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.*;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionsResponse;
import io.github.cristhianm30.heikoh.application.mapper.TransactionMapper;
import io.github.cristhianm30.heikoh.application.service.TransactionService;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionsData;
import io.github.cristhianm30.heikoh.domain.port.in.DeleteTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.TransactionServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.UpdateTransactionServicePort;
import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_EXPENSE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_INCOME;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final RegisterTransactionServicePort registerTransactionServicePort;
    private final UpdateTransactionServicePort updateTransactionServicePort;
    private final DeleteTransactionServicePort deleteTransactionServicePort;
    private final TransactionServicePort transactionServicePort;
    private final TransactionMapper transactionMapper;

    @Override
    public Flux<TransactionsResponse> getTransactions(Long userId, TransactionsRequest request) {

        TransactionsData data = transactionMapper.toTransactionData(request);
        return transactionServicePort.getTransactions(userId, data)
                .map(this::mapToTransactionsResponse);
    }

    @Override
    public Mono<TransactionResponse> getTransactionDetail(Long userId, Long transactionId, String type) {
        return transactionServicePort.getTransactionDetail(userId, transactionId, type)
                .map(this::mapToTransactionResponse);
    }


    @Override
    public Mono<TransactionResponse> updateTransaction(Long userId, Long transactionId, String type, Object updateRequest) {
        try {
            Object model = mapRequestToModel(type, updateRequest);
            return updateTransactionServicePort.updateTransaction(userId, transactionId, type, model)
                    .map(this::mapToTransactionResponse);
        } catch (InvalidTransactionTypeException e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Void> deleteTransaction(Long userId, Long transactionId, String type) {
        if (!TYPE_EXPENSE.equalsIgnoreCase(type) && !TYPE_INCOME.equalsIgnoreCase(type)) {
            return Mono.error(new InvalidTransactionTypeException("Invalid transaction type: " + type));
        }
        return deleteTransactionServicePort.deleteTransaction(userId, transactionId, type);
    }

    @Override
    public Mono<TransactionResponse> registerTransaction(Long userId, String type, Object registerRequest) {
        try {
            Object model = mapRequestToModel(type, registerRequest);
            return registerTransactionServicePort.registerTransaction(userId, type, model)
                    .map(this::mapToTransactionResponse);
        } catch (InvalidTransactionTypeException e) {
            return Mono.error(e);
        }
    }

    private TransactionsResponse mapToTransactionsResponse(Object obj) {
        if (obj instanceof ExpenseModel) {
            return transactionMapper.toTransactionsResponse((ExpenseModel) obj);
        } else if (obj instanceof IncomeModel) {
            return transactionMapper.toTransactionsResponse((IncomeModel) obj);
        }
        return null;
    }

    private TransactionResponse mapToTransactionResponse(Object obj) {
        if (obj instanceof ExpenseModel) {
            return transactionMapper.toTransactionResponse((ExpenseModel) obj);
        } else if (obj instanceof IncomeModel) {
            return transactionMapper.toTransactionResponse((IncomeModel) obj);
        }
        return null;
    }

    private Object mapRequestToModel(String type, Object request) {
        if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            if (request instanceof RegisterExpenseRequest) {
                return transactionMapper.toExpenseModel((RegisterExpenseRequest) request);
            } else if (request instanceof UpdateExpenseRequest) {
                return transactionMapper.toExpenseModel((UpdateExpenseRequest) request);
            }
        } else if (TYPE_INCOME.equalsIgnoreCase(type)) {
            if (request instanceof RegisterIncomeRequest) {
                return transactionMapper.toIncomeModel((RegisterIncomeRequest) request);
            } else if (request instanceof UpdateIncomeRequest) {
                return transactionMapper.toIncomeModel((UpdateIncomeRequest) request);
            }
        }
        throw new InvalidTransactionTypeException("Invalid request type for transaction.");
    }
}






