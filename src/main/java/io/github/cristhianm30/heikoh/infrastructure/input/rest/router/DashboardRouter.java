package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.DashboardHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.DASHBOARD_BASE_PATH;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class DashboardRouter {

    private final DashboardHandler dashboardHandler;

    @Bean
    public RouterFunction<ServerResponse> dashboardRoutes() {
        return route(GET(DASHBOARD_BASE_PATH + "/summary").and(accept(MediaType.APPLICATION_JSON)),
                dashboardHandler::getFinancialSummary);
    }
}
