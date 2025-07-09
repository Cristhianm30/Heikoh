package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.*;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;

import java.net.URI;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathVariableConstant.TRANSACTION_ID;
import static io.github.cristhianm30.heikoh.domain.util.constant.QueryParamConstant.*;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_EXPENSE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_INCOME;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final TransactionService transactionService;
    private final ValidateRequest validateRequest;

    private Mono<?> extractRequestBodyMono(ServerRequest request, String type, Class<?> expenseClass, Class<?> incomeClass) {
        if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            return request.bodyToMono(expenseClass);
        } else if (TYPE_INCOME.equalsIgnoreCase(type)) {
            return request.bodyToMono(incomeClass);
        } else {
            return Mono.error(new InvalidTransactionTypeException("Invalid transaction type in path"));
        }
    }

    private Mono<Tuple2<Object, AuthenticatedUser>> processTransactionRequest(ServerRequest request, String type, Class<?> expenseClass, Class<?> incomeClass) {
        return extractRequestBodyMono(request, type, expenseClass, incomeClass)
                .doOnNext(validateRequest::validate)
                .zipWith(request.principal())
                .flatMap(tuple -> {
                    Object dto = tuple.getT1();
                    AuthenticatedUser user = (AuthenticatedUser) ((Authentication) tuple.getT2()).getPrincipal();
                    return Mono.just(Tuples.of(dto, user));
                });
    }

    public Mono<ServerResponse> getTransactions(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .cast(AuthenticatedUser.class)
                .flatMapMany(authenticatedUser -> {
                    Long userId = authenticatedUser.getId();
                    TransactionsRequest transactionsRequest = TransactionsRequest.builder()
                            .year(request.queryParam(YEAR).map(Integer::parseInt).orElse(INTEGER_NULL))
                            .month(request.queryParam(MONTH).map(Integer::parseInt).orElse(INTEGER_NULL))
                            .limit(request.queryParam(LIMIT).map(Integer::parseInt).orElse(DEFAULT_LIMIT))
                            .offset(request.queryParam(OFFSET).map(Integer::parseInt).orElse(DEFAULT_OFFSET))
                            .type(request.queryParam(TYPE).orElse(STRING_NULL))
                            .build();
                    validateRequest.validate(transactionsRequest);
                    return transactionService.getTransactions(userId, transactionsRequest);
                })
                .collectList()
                .flatMap(transactionResponses -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionResponses));
    }

    public Mono<ServerResponse> getTransactionDetail(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .cast(AuthenticatedUser.class)
                .flatMap(authenticatedUser -> {
                    Long userId = authenticatedUser.getId();
                    Long transactionId = Long.parseLong(request.pathVariable(TRANSACTION_ID));
                    String type = request.queryParam(TYPE).orElse(STRING_NULL);
                    return transactionService.getTransactionDetail(userId, transactionId, type);
                })
                .flatMap(transactionResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> registerTransaction(ServerRequest request) {
        String type = request.pathVariable(TYPE);

        return processTransactionRequest(request, type, RegisterExpenseRequest.class, RegisterIncomeRequest.class)
                .flatMap(tuple -> {
                    Object dto = tuple.getT1();
                    AuthenticatedUser user = tuple.getT2();
                    return transactionService.registerTransaction(user.getId(), type, dto);
                })
                .flatMap(response -> ServerResponse
                        .created(URI.create(String.format("/api/v1/transactions/%s/%d", type, response.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(InvalidTransactionTypeException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateTransaction(ServerRequest request) {
        String type = request.pathVariable(TYPE);
        Long transactionId = Long.valueOf(request.pathVariable(TRANSACTION_ID));

        return processTransactionRequest(request, type, UpdateExpenseRequest.class, UpdateIncomeRequest.class)
                .flatMap(tuple -> {
                    Object dto = tuple.getT1();
                    AuthenticatedUser user = tuple.getT2();
                    return transactionService.updateTransaction(user.getId(), transactionId, type, dto);
                })
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(InvalidTransactionTypeException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage()));
    }


    public Mono<ServerResponse> deleteTransaction(ServerRequest request) {
        String type = request.pathVariable(TYPE);
        Long transactionId = Long.valueOf(request.pathVariable(TRANSACTION_ID));

        return request.principal()
                .flatMap(principal -> {
                    AuthenticatedUser user = (AuthenticatedUser) ((Authentication) principal).getPrincipal();
                    return transactionService.deleteTransaction(user.getId(), transactionId, type);
                })
                .then(ServerResponse.noContent().build());
    }

}
