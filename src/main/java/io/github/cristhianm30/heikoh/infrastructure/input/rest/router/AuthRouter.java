package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.AuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.USER_LOGIN;
import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.USER_REGISTER;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouter {

    private final AuthHandler authHandler;

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return route(POST(USER_REGISTER)
                        .and(accept(MediaType.APPLICATION_JSON)),
                authHandler::registerUser)
                .andRoute(POST(USER_LOGIN)
                                .and(accept(MediaType.APPLICATION_JSON)),
                        authHandler::loginUser);

    }
}
