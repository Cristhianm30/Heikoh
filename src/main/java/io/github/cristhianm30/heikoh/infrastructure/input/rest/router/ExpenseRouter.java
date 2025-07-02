package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.ExpenseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class ExpenseRouter {

    private final ExpenseHandler expenseHandler;

    @Bean
    public RouterFunction<ServerResponse> expenseRoutes() {
        return route(POST("/api/v1/expenses")
                        .and(accept(MediaType.APPLICATION_JSON)),
                expenseHandler::registerExpense);
    }
}
