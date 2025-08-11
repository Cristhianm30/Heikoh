package io.github.cristhianm30.heikoh.domain.port.in;

import reactor.core.publisher.Mono;

public interface DeleteTransactionServicePort {

    Mono<Void> deleteTransaction(Long userId, Long transactionId, String type);
}