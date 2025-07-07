package io.github.cristhianm30.heikoh.application.service;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import reactor.core.publisher.Flux;

public interface TransactionService {
    Flux<TransactionResponse> getTransactions(Long userId, TransactionRequest request);
}
