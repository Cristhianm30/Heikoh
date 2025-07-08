package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.ExpenseRequest;
import io.github.cristhianm30.heikoh.application.dto.response.ExpenseResponse;
import io.github.cristhianm30.heikoh.application.service.ExpenseService;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import io.github.cristhianm30.heikoh.domain.exception.RequestValidationException;
import io.github.cristhianm30.heikoh.domain.exception.ExpenseNotRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ExpenseHandlerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private ValidateRequest validateRequest;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExpenseHandler expenseHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerExpense_shouldReturnCreatedStatus() {
        // Given
        ExpenseRequest expenseRequest = new ExpenseRequest();
        ExpenseResponse expenseResponse = new ExpenseResponse();
        expenseResponse.setId(1L);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "test@example.com");

        when(serverRequest.bodyToMono(ExpenseRequest.class)).thenReturn(Mono.just(expenseRequest));
        doNothing().when(validateRequest).validate(any(ExpenseRequest.class));
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        doReturn(Mono.just(authentication)).when(serverRequest).principal();
        when(expenseService.registerExpense(any(ExpenseRequest.class), anyLong())).thenReturn(Mono.just(expenseResponse));

        // When
        Mono<ServerResponse> responseMono = expenseHandler.registerExpense(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CREATED) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registerExpense_shouldReturnBadRequest_whenValidationFails() {
        // Given
        ExpenseRequest expenseRequest = new ExpenseRequest();
        when(serverRequest.bodyToMono(ExpenseRequest.class)).thenReturn(Mono.error(new RequestValidationException("Validation error")));
        doReturn(Mono.just(authentication)).when(serverRequest).principal();

        // When
        Mono<ServerResponse> responseMono = expenseHandler.registerExpense(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof RequestValidationException &&
                        throwable.getMessage().equals("Validation error")
                )
                .verify();
    }

    @Test
    void registerExpense_shouldReturnInternalServerError_whenServiceFails() {
        // Given
        ExpenseRequest expenseRequest = new ExpenseRequest();
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "test@example.com");

        when(serverRequest.bodyToMono(ExpenseRequest.class)).thenReturn(Mono.just(expenseRequest));
        doNothing().when(validateRequest).validate(any(ExpenseRequest.class));
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        doReturn(Mono.just(authentication)).when(serverRequest).principal();
        when(expenseService.registerExpense(any(ExpenseRequest.class), anyLong())).thenReturn(Mono.error(new ExpenseNotRegisteredException("Service error")));

        // When
        Mono<ServerResponse> responseMono = expenseHandler.registerExpense(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ExpenseNotRegisteredException &&
                        throwable.getMessage().equals("Service error")
                )
                .verify();
    }
}