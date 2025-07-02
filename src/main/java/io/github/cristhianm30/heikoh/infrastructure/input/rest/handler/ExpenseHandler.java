package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.ExpenseRequest;
import io.github.cristhianm30.heikoh.application.service.ExpenseService;
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
public class ExpenseHandler {

    private final ExpenseService expenseService;

    public Mono<ServerResponse> registerExpense(ServerRequest request) {
        return request.bodyToMono(ExpenseRequest.class)
                .zipWith(request.principal())
                .flatMap(tuple -> {
                    ExpenseRequest expenseRequest = tuple.getT1();
                    AuthenticatedUser authenticatedUser = (AuthenticatedUser) ((org.springframework.security.core.Authentication) tuple.getT2()).getPrincipal();
                    return expenseService.registerExpense(expenseRequest, authenticatedUser.getId())
                            .flatMap(expenseResponse -> ServerResponse.created(URI.create("/api/v1/expenses/" + expenseResponse.getId()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(expenseResponse));
                });
    }
}
