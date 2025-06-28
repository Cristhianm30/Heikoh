package io.github.cristhianm30.heikoh.infrastructure.infrastructure.input.rest.router;

import io.github.cristhianm30.heikoh.infrastructure.infrastructure.input.rest.handler.RegisterUserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.USER_REGISTER;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RegisterUserRouter {

    private final RegisterUserHandler registerUserHandler;

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return route(POST(USER_REGISTER)
                        .and(accept(MediaType.APPLICATION_JSON)),
                registerUserHandler::registerUser);

    }
}
