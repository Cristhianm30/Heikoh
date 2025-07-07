package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.TransactionData;
import reactor.core.publisher.Flux;

public interface GetTransactionsServicePort {
    Flux<Object> getTransactions(Long userId, TransactionData request);
}
