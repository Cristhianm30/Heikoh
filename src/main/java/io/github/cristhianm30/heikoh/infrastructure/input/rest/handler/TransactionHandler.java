package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionRequest;
import io.github.cristhianm30.heikoh.application.service.TransactionService;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.heikoh.domain.util.constant.QueryParamConstant.*;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final TransactionService transactionService;
    private final ValidateRequest validateRequest;

    public Mono<ServerResponse> getTransactions(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .cast(AuthenticatedUser.class)
                .flatMapMany(authenticatedUser -> {
                    Long userId = authenticatedUser.getId();
                    TransactionRequest transactionRequest = TransactionRequest.builder()
                            .year(request.queryParam(YEAR).map(Integer::parseInt).orElse(INTEGER_NULL))
                            .month(request.queryParam(MONTH).map(Integer::parseInt).orElse(INTEGER_NULL))
                            .limit(request.queryParam(LIMIT).map(Integer::parseInt).orElse(DEFAULT_LIMIT))
                            .offset(request.queryParam(OFFSET).map(Integer::parseInt).orElse(DEFAULT_OFFSET))
                            .type(request.queryParam(TYPE).orElse(STRING_NULL))
                            .build();
                    validateRequest.validate(transactionRequest);
                    return transactionService.getTransactions(userId, transactionRequest);
                })
                .collectList()
                .flatMap(transactionResponses -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionResponses));
    }
}
