package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.ExpenseRequest;
import io.github.cristhianm30.heikoh.application.service.ExpenseService;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.EXPENSE_REGISTER;

@Component
@RequiredArgsConstructor
public class ExpenseHandler {

    private final ExpenseService expenseService;
    private final ValidateRequest validateRequest;

    public Mono<ServerResponse> registerExpense(ServerRequest request) {
        return request.bodyToMono(ExpenseRequest.class)
                .doOnNext(validateRequest::validate)
                .zipWith(request.principal())
                .flatMap(tuple -> {
                    ExpenseRequest expenseRequest = tuple.getT1();
                    AuthenticatedUser authenticatedUser = (AuthenticatedUser) ((Authentication) tuple.getT2()).getPrincipal();
                    return expenseService.registerExpense(expenseRequest, authenticatedUser.getId())
                            .flatMap(expenseResponse -> ServerResponse.created(URI.create(EXPENSE_REGISTER + expenseResponse.getId()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(expenseResponse));
                });
    }
}
