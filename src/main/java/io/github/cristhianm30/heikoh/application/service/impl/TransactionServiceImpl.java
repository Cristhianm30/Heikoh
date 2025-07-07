package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.mapper.TransactionMapper;
import io.github.cristhianm30.heikoh.application.service.TransactionService;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionData;
import io.github.cristhianm30.heikoh.domain.port.in.GetTransactionsServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final GetTransactionsServicePort getTransactionsServicePort;
    private final TransactionMapper transactionMapper;

    @Override
    public Flux<TransactionResponse> getTransactions(Long userId, TransactionRequest request) {

        TransactionData data = transactionMapper.toTransactionData(request);
        return getTransactionsServicePort.getTransactions(userId, data)
                .map(this::mapToTransactionResponse);
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

