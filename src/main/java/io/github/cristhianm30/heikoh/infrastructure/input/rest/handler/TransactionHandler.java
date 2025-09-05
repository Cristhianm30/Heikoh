package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.*;
import io.github.cristhianm30.heikoh.application.dto.response.ExpenseResponse;
import io.github.cristhianm30.heikoh.application.dto.response.IncomeResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.service.TransactionService;
import io.github.cristhianm30.heikoh.domain.exception.InvalidTransactionTypeException;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import io.github.cristhianm30.heikoh.infrastructure.exception.ErrorResponse;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INVALID_TRANSACTION_TYPE_IN_PATH;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.QUERY_PARAM_TYPE_REQUIRED;
import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.TRANSACTION_URI_FORMAT;
import static io.github.cristhianm30.heikoh.domain.util.constant.PathVariableConstant.TRANSACTION_ID;
import static io.github.cristhianm30.heikoh.domain.util.constant.QueryParamConstant.TYPE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_EXPENSE;
import static io.github.cristhianm30.heikoh.domain.util.constant.TransactionConstant.TYPE_INCOME;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final TransactionService transactionService;
    private final ValidateRequest validateRequest;

    @Operation(operationId = "getTransactions", summary = "Get all transactions for the user",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "year", schema = @Schema(type = "integer"), description = "Filter by year"),
                    @Parameter(in = ParameterIn.QUERY, name = "month", schema = @Schema(type = "integer"), description = "Filter by month (1-12)"),
                    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer"), description = "Number of records to return"),
                    @Parameter(in = ParameterIn.QUERY, name = "offset", schema = @Schema(type = "integer"), description = "Offset for pagination"),
                    @Parameter(in = ParameterIn.QUERY, name = "type", schema = @Schema(type = "string", allowableValues = {"income", "expense"}), description = "Filter by transaction type")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(schema = @Schema(implementation = TransactionResponse.class)))
            })
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

    @Operation(operationId = "getTransactionDetail", summary = "Get transaction details by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "transactionId", required = true, schema = @Schema(type = "integer", format = "int64"), description = "ID of the transaction"),
                    @Parameter(in = ParameterIn.QUERY, name = "type", required = true, schema = @Schema(type = "string", allowableValues = {"income", "expense"}), description = "Type of the transaction")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction found", content = {
                            @Content(mediaType = "application/json", schema = @Schema(oneOf = {IncomeResponse.class, ExpenseResponse.class}))
                    }),
                    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ServerResponse> getTransactionDetail(ServerRequest request) {
        return withAuthenticatedUser(request, user -> {
            Long transactionId = Long.parseLong(request.pathVariable(TRANSACTION_ID));
            String type = request.queryParam(TYPE)
                    .orElseThrow(() -> new IllegalArgumentException(QUERY_PARAM_TYPE_REQUIRED));

            return transactionService.getTransactionDetail(user.getId(), transactionId, type)
                    .flatMap(response -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response))
                    .switchIfEmpty(ServerResponse.notFound().build());
        });
    }

    @Operation(operationId = "registerTransaction", summary = "Register a new transaction (income or expense)",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "type", required = true, schema = @Schema(type = "string", allowableValues = {"income", "expense"}), description = "Type of transaction to register")
            },
            requestBody = @RequestBody(description = "Transaction data. Use RegisterIncomeRequest for 'income' and RegisterExpenseRequest for 'expense'", required = true, content = @Content(schema = @Schema(oneOf = {RegisterIncomeRequest.class, RegisterExpenseRequest.class}))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transaction created", content = @Content(schema = @Schema(oneOf = {IncomeResponse.class, ExpenseResponse.class}))),
                    @ApiResponse(responseCode = "400", description = "Invalid transaction type or bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ServerResponse> registerTransaction(ServerRequest request) {
        String type = request.pathVariable(TYPE);
        Mono<?> dtoMono = getDtoMono(request, type, RegisterExpenseRequest.class, RegisterIncomeRequest.class);

        return withAuthenticatedUser(request, user ->
                dtoMono
                        .doOnNext(validateRequest::validate)
                        .flatMap(dto -> transactionService.registerTransaction(user.getId(), type, dto))
                        .flatMap(response -> ServerResponse
                                .created(URI.create(String.format(TRANSACTION_URI_FORMAT, type, response.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response))
        ).onErrorResume(InvalidTransactionTypeException.class, e ->
                ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    @Operation(operationId = "updateTransaction", summary = "Update an existing transaction",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "type", required = true, schema = @Schema(type = "string", allowableValues = {"income", "expense"}), description = "Type of transaction to update"),
                    @Parameter(in = ParameterIn.PATH, name = "transactionId", required = true, schema = @Schema(type = "integer", format = "int64"), description = "ID of the transaction to update")
            },
            requestBody = @RequestBody(description = "Updated transaction data. Use UpdateIncomeRequest for 'income' and UpdateExpenseRequest for 'expense'", required = true, content = @Content(schema = @Schema(oneOf = {UpdateIncomeRequest.class, UpdateExpenseRequest.class}))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction updated", content = @Content(schema = @Schema(oneOf = {IncomeResponse.class, ExpenseResponse.class}))),
                    @ApiResponse(responseCode = "400", description = "Invalid transaction type or bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
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

    @Operation(operationId = "deleteTransaction", summary = "Delete a transaction",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "type", required = true, schema = @Schema(type = "string", allowableValues = {"income", "expense"}), description = "Type of transaction to delete"),
                    @Parameter(in = ParameterIn.PATH, name = "transactionId", required = true, schema = @Schema(type = "integer", format = "int64"), description = "ID of the transaction to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ServerResponse> deleteTransaction(ServerRequest request) {
        return withAuthenticatedUser(request, user -> {
            String type = request.pathVariable(TYPE);
            Long transactionId = Long.valueOf(request.pathVariable(TRANSACTION_ID));
            return transactionService.deleteTransaction(user.getId(), transactionId, type)
                    .then(ServerResponse.noContent().build());
        });
    }

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
            return Mono.error(new InvalidTransactionTypeException(INVALID_TRANSACTION_TYPE_IN_PATH + type));
        }
    }

}
