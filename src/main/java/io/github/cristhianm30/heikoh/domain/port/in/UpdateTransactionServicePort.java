package io.github.cristhianm30.heikoh.domain.port.in;

import reactor.core.publisher.Mono;

public interface UpdateTransactionServicePort {

    Mono<Object> updateTransaction(Long userId, Long transactionId, String type, Object transactionData);
}