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
import java.util.function.Function;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathVariableConstant.TRANSACTION_ID;
import static io.github.cristhianm30.heikoh.domain.util.constant.QueryParamConstant.*;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_EXPENSE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_INCOME;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final TransactionService transactionService;
    private final ValidateRequest validateRequest;

    public Mono<ServerResponse> getTransactions(ServerRequest request) {
        return withAuthenticatedUser(request, user ->
                request.bind(TransactionsRequest.class)
                        .doOnNext(validateRequest::validate)
                        .flatMapMany(req -> transactionService.getTransactions(user.getId(), req))
                        .collectList()
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
        );
    }

    public Mono<ServerResponse> getTransactionDetail(ServerRequest request) {
        return withAuthenticatedUser(request, user -> {
            Long transactionId = Long.parseLong(request.pathVariable(TRANSACTION_ID));
            String type = request.queryParam(TYPE)
                    .orElseThrow(() -> new IllegalArgumentException("Query param 'type' is required."));

            return transactionService.getTransactionDetail(user.getId(), transactionId, type)
                    .flatMap(response -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response))
                    .switchIfEmpty(ServerResponse.notFound().build());
        });
    }

    public Mono<ServerResponse> registerTransaction(ServerRequest request) {
        String type = request.pathVariable(TYPE);
        Mono<?> dtoMono = getDtoMono(request, type, RegisterExpenseRequest.class, RegisterIncomeRequest.class);

        return withAuthenticatedUser(request, user ->
                dtoMono
                        .doOnNext(validateRequest::validate)
                        .flatMap(dto -> transactionService.registerTransaction(user.getId(), type, dto))
                        .flatMap(response -> ServerResponse
                                .created(URI.create(String.format("/api/v1/transactions/%s/%d", type, response.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
        ).onErrorResume(InvalidTransactionTypeException.class, e ->
                ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateTransaction(ServerRequest request) {
        String type = request.pathVariable(TYPE);
        Long transactionId = Long.valueOf(request.pathVariable(TRANSACTION_ID));
        Mono<?> dtoMono = getDtoMono(request, type, UpdateExpenseRequest.class, UpdateIncomeRequest.class);

        return withAuthenticatedUser(request, user ->
                dtoMono
                        .doOnNext(validateRequest::validate)
                        .flatMap(dto -> transactionService.updateTransaction(user.getId(), transactionId, type, dto))
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
                        .switchIfEmpty(ServerResponse.notFound().build())
        ).onErrorResume(InvalidTransactionTypeException.class, e ->
                ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> deleteTransaction(ServerRequest request) {
        return withAuthenticatedUser(request, user -> {
            String type = request.pathVariable(TYPE);
            Long transactionId = Long.valueOf(request.pathVariable(TRANSACTION_ID));
            return transactionService.deleteTransaction(user.getId(), transactionId, type)
                    .then(ServerResponse.noContent().build());
        });
    }

    // --- MÉTODOS PRIVADOS DE AYUDA ---

    private Mono<ServerResponse> withAuthenticatedUser(ServerRequest request,
                                                       Function<AuthenticatedUser, Mono<ServerResponse>> action) {
        return request.principal()
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .cast(AuthenticatedUser.class)
                .flatMap(action);
    }


    private Mono<?> getDtoMono(ServerRequest request, String type, Class<?> expenseClass, Class<?> incomeClass) {
        if (TYPE_EXPENSE.equalsIgnoreCase(type)) {
            return request.bodyToMono(expenseClass);
        } else if (TYPE_INCOME.equalsIgnoreCase(type)) {
            return request.bodyToMono(incomeClass);
        } else {
            return Mono.error(new InvalidTransactionTypeException("Invalid transaction type in path: " + type));
        }
    }

}
