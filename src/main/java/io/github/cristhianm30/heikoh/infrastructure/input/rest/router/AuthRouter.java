package io.github.cristhianm30.heikoh.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.input.rest.handler.AuthHandler;
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
public class AuthRouter {

    private final AuthHandler authHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(path = AUTH_BASE_PATH + AUTH_REGISTER_ENDPOINT_PATH, method = RequestMethod.POST, beanClass = AuthHandler.class, beanMethod = "registerUser"),
            @RouterOperation(path = AUTH_BASE_PATH + AUTH_LOGIN_ENDPOINT_PATH, method = RequestMethod.POST, beanClass = AuthHandler.class, beanMethod = "loginUser"),
            @RouterOperation(path = AUTH_BASE_PATH + AUTH_REFRESH_ENDPOINT_PATH, method = RequestMethod.GET, beanClass = AuthHandler.class, beanMethod = "refreshToken")
    })
    public RouterFunction<ServerResponse> authRoutes() {
        return nest(path(AUTH_BASE_PATH),
                route()
                        .POST(AUTH_REGISTER_ENDPOINT_PATH,
                                accept(MediaType.APPLICATION_JSON), authHandler::registerUser)
                        .POST(AUTH_LOGIN_ENDPOINT_PATH,
                                accept(MediaType.APPLICATION_JSON), authHandler::loginUser)
                        .GET(AUTH_REFRESH_ENDPOINT_PATH,
                                accept(MediaType.APPLICATION_JSON), authHandler::refreshToken)
                        .build()
        );
    }
}
