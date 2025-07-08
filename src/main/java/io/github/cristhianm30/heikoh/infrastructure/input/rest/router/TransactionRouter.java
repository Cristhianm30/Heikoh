package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class TransactionRouter {

    private final TransactionHandler transactionHandler;

    @Bean
    public RouterFunction<ServerResponse> transactionRoutes() {
        return route(GET(TRANSACTIONS),
                transactionHandler::getTransactions)
                .andRoute(GET(TRANSACTION_DETAIL+TRANSACTION_ID_PATH),
                        transactionHandler::getTransactionDetail)
                ;

    }

}
