package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionsRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionsResponse;
import io.github.cristhianm30.heikoh.application.mapper.TransactionMapper;
import io.github.cristhianm30.heikoh.application.service.TransactionService;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionsData;
import io.github.cristhianm30.heikoh.domain.port.in.TransactionServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

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
}






