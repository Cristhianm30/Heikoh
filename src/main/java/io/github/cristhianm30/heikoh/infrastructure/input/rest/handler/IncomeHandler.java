package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.IncomeRequest;
import io.github.cristhianm30.heikoh.application.service.IncomeService;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class IncomeHandler {

    private final IncomeService incomeService;

    public Mono<ServerResponse> registerIncome(ServerRequest request) {
        return request.bodyToMono(IncomeRequest.class)
                .zipWith(request.principal())
                .flatMap(tuple -> {
                    IncomeRequest incomeRequest = tuple.getT1();
                    AuthenticatedUser authenticatedUser = (AuthenticatedUser) ((org.springframework.security.core.Authentication) tuple.getT2()).getPrincipal();
                    return incomeService.registerIncome(incomeRequest, authenticatedUser.getId())
                            .flatMap(incomeResponse -> ServerResponse.created(URI.create("/api/v1/incomes/" + incomeResponse.getId()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(incomeResponse));
                });
    }
}
