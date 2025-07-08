package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.IncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.response.IncomeResponse;
import io.github.cristhianm30.heikoh.application.service.IncomeService;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import io.github.cristhianm30.heikoh.domain.exception.RequestValidationException;
import io.github.cristhianm30.heikoh.domain.exception.IncomeNotRegisteredException;
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

class IncomeHandlerTest {

    @Mock
    private IncomeService incomeService;

    @Mock
    private ValidateRequest validateRequest;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private IncomeHandler incomeHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerIncome_shouldReturnCreatedStatus() {
        // Given
        IncomeRequest incomeRequest = new IncomeRequest();
        IncomeResponse incomeResponse = new IncomeResponse();
        incomeResponse.setId(1L);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "test@example.com");

        when(serverRequest.bodyToMono(IncomeRequest.class)).thenReturn(Mono.just(incomeRequest));
        doNothing().when(validateRequest).validate(any(IncomeRequest.class));
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        doReturn(Mono.just(authentication)).when(serverRequest).principal();
        when(incomeService.registerIncome(any(IncomeRequest.class), anyLong())).thenReturn(Mono.just(incomeResponse));

        // When
        Mono<ServerResponse> responseMono = incomeHandler.registerIncome(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CREATED) &&
                                serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON)
                )
                .verifyComplete();
    }

    @Test
    void registerIncome_shouldReturnBadRequest_whenValidationFails() {
        // Given
        IncomeRequest incomeRequest = new IncomeRequest();
        when(serverRequest.bodyToMono(IncomeRequest.class)).thenReturn(Mono.error(new RequestValidationException("Validation error")));
        doReturn(Mono.just(authentication)).when(serverRequest).principal();

        // When
        Mono<ServerResponse> responseMono = incomeHandler.registerIncome(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof RequestValidationException &&
                        throwable.getMessage().equals("Validation error")
                )
                .verify();
    }

    @Test
    void registerIncome_shouldReturnInternalServerError_whenServiceFails() {
        // Given
        IncomeRequest incomeRequest = new IncomeRequest();
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "test@example.com");

        when(serverRequest.bodyToMono(IncomeRequest.class)).thenReturn(Mono.just(incomeRequest));
        doNothing().when(validateRequest).validate(any(IncomeRequest.class));
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        doReturn(Mono.just(authentication)).when(serverRequest).principal();
        when(incomeService.registerIncome(any(IncomeRequest.class), anyLong())).thenReturn(Mono.error(new IncomeNotRegisteredException("Service error")));

        // When
        Mono<ServerResponse> responseMono = incomeHandler.registerIncome(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof IncomeNotRegisteredException &&
                        throwable.getMessage().equals("Service error")
                )
                .verify();
    }
}