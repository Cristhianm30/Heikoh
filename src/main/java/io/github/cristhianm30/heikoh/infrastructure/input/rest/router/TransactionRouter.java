package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class TransactionRouter {

    private final TransactionHandler transactionHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(path = TRANSACTION_BASE_PATH + TRANSACTION_LIST_ENDPOINT_PATH, method = RequestMethod.GET, beanClass = TransactionHandler.class, beanMethod = "getTransactions"),
            @RouterOperation(path = TRANSACTION_BASE_PATH + TRANSACTION_DETAIL_ENDPOINT_PATH, method = RequestMethod.GET, beanClass = TransactionHandler.class, beanMethod = "getTransactionDetail"),
            @RouterOperation(path = TRANSACTION_BASE_PATH + TRANSACTION_CREATE_ENDPOINT_PATH, method = RequestMethod.POST, beanClass = TransactionHandler.class, beanMethod = "registerTransaction"),
            @RouterOperation(path = TRANSACTION_BASE_PATH + TRANSACTION_UPDATE_ENDPOINT_PATH, method = RequestMethod.PUT, beanClass = TransactionHandler.class, beanMethod = "updateTransaction"),
            @RouterOperation(path = TRANSACTION_BASE_PATH + TRANSACTION_DELETE_ENDPOINT_PATH, method = RequestMethod.DELETE, beanClass = TransactionHandler.class, beanMethod = "deleteTransaction")
    })
    public RouterFunction<ServerResponse> transactionRoutes() {
        return nest(path(TRANSACTION_BASE_PATH),
                route()
                        .GET(TRANSACTION_LIST_ENDPOINT_PATH,
                                transactionHandler::getTransactions)
                        .GET(TRANSACTION_DETAIL_ENDPOINT_PATH,
                                transactionHandler::getTransactionDetail)
                        .POST(TRANSACTION_CREATE_ENDPOINT_PATH,
                                accept(MediaType.APPLICATION_JSON), transactionHandler::registerTransaction)
                        .PUT(TRANSACTION_UPDATE_ENDPOINT_PATH,
                                accept(MediaType.APPLICATION_JSON), transactionHandler::updateTransaction)
                        .DELETE(TRANSACTION_DELETE_ENDPOINT_PATH,
                                transactionHandler::deleteTransaction)
                        .build()
        );
    }

}
