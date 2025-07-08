package io.github.cristhianm30.heikoh.application.service;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionsRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionsResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Flux<TransactionsResponse> getTransactions(Long userId, TransactionsRequest request);
    Mono<TransactionResponse> getTransactionDetail(Long userId, Long transactionId, String type);
}
