package io.github.cristhianm30.heikoh.domain.port.in;

import reactor.core.publisher.Mono;


public interface RegisterTransactionServicePort {

    Mono<Object> registerTransaction(Long userId, String type, Object transactionData);
}