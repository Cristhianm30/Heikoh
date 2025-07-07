package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.service.TransactionService;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private ValidateRequest validateRequest;

    @InjectMocks
    private TransactionHandler transactionHandler;

    private ServerRequest serverRequest;
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    void setUp() {
        serverRequest = mock(ServerRequest.class);
        authenticatedUser = new AuthenticatedUser(1L, "testuser");

        Principal mockPrincipal = mock(Principal.class, withSettings().extraInterfaces(Authentication.class));
        Authentication mockAuthentication = (Authentication) mockPrincipal;

        when(mockAuthentication.getPrincipal()).thenReturn(authenticatedUser);
        doReturn(Mono.just(mockAuthentication)).when(serverRequest).principal();
    }

    @Test
    void getTransactions_ShouldReturnTransactions() {
        TransactionResponse transactionResponse1 = TransactionResponse.builder()
                .type("Gasto")
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now())
                .build();
        TransactionResponse transactionResponse2 = TransactionResponse.builder()
                .type("Ingreso")
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .build();

        when(serverRequest.queryParam("year")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("month")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("limit")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("offset")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("type")).thenReturn(Optional.empty());

        when(transactionService.getTransactions(anyLong(), any(TransactionRequest.class)))
                .thenReturn(Flux.just(transactionResponse1, transactionResponse2));

        StepVerifier.create(transactionHandler.getTransactions(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldHandleQueryParams() {
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .type("Gasto")
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now())
                .build();

        when(serverRequest.queryParam("year")).thenReturn(Optional.of("2023"));
        when(serverRequest.queryParam("month")).thenReturn(Optional.of("1"));
        when(serverRequest.queryParam("limit")).thenReturn(Optional.of("5"));
        when(serverRequest.queryParam("offset")).thenReturn(Optional.of("0"));
        when(serverRequest.queryParam("type")).thenReturn(Optional.empty());

        when(transactionService.getTransactions(anyLong(), any(TransactionRequest.class)))
                .thenReturn(Flux.just(transactionResponse));

        StepVerifier.create(transactionHandler.getTransactions(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldFilterByType() {
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .type("Ingreso")
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .build();

        when(serverRequest.queryParam("year")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("month")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("limit")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("offset")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("type")).thenReturn(Optional.of("income"));

        when(transactionService.getTransactions(anyLong(), any(TransactionRequest.class)))
                .thenReturn(Flux.just(transactionResponse));

        StepVerifier.create(transactionHandler.getTransactions(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }
}

