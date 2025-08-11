package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.TransactionsData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionServicePort {
    Flux<Object> getTransactions(Long userId, TransactionsData request);
    Mono<Object> getTransactionDetail(Long userId, Long transactionId, String type);
}
