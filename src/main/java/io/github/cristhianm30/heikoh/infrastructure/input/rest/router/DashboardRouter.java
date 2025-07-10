package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.DashboardHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.DASHBOARD_BASE_PATH;
import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.DASHBOARD_EXPENSES_SUMMARY_BY_PATH;
import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.DASHBOARD_SUMMARY_PATH;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class DashboardRouter {

    private final DashboardHandler dashboardHandler;

    @Bean
    public RouterFunction<ServerResponse> dashboardRoutes() {
        return nest(path(DASHBOARD_BASE_PATH),
                route()
                        .GET(DASHBOARD_SUMMARY_PATH, accept(MediaType.APPLICATION_JSON), dashboardHandler::getFinancialSummary)
                        .GET(DASHBOARD_EXPENSES_SUMMARY_BY_PATH, accept(MediaType.APPLICATION_JSON), dashboardHandler::getExpenseAggregation)
                        .build()
        );
    }
}
