package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.DashboardHandler;
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
public class DashboardRouter {

    private final DashboardHandler dashboardHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(path = DASHBOARD_BASE_PATH + DASHBOARD_SUMMARY_PATH, method = RequestMethod.GET, beanClass = DashboardHandler.class, beanMethod = "getFinancialSummary"),
            @RouterOperation(path = DASHBOARD_BASE_PATH + DASHBOARD_EXPENSES_SUMMARY_BY_PATH, method = RequestMethod.GET, beanClass = DashboardHandler.class, beanMethod = "getExpenseAggregation"),
            @RouterOperation(path = DASHBOARD_BASE_PATH + DASHBOARD_INCOMES_SUMMARY_BY_PATH, method = RequestMethod.GET, beanClass = DashboardHandler.class, beanMethod = "getIncomeAggregation")
    })
    public RouterFunction<ServerResponse> dashboardRoutes() {
        return nest(path(DASHBOARD_BASE_PATH),
                route()
                        .GET(DASHBOARD_SUMMARY_PATH, accept(MediaType.APPLICATION_JSON), dashboardHandler::getFinancialSummary)
                        .GET(DASHBOARD_EXPENSES_SUMMARY_BY_PATH, accept(MediaType.APPLICATION_JSON), dashboardHandler::getExpenseAggregation)
                        .GET(DASHBOARD_INCOMES_SUMMARY_BY_PATH, accept(MediaType.APPLICATION_JSON), dashboardHandler::getIncomeAggregation)
                        .build()
        );
    }
}
