package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionsRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionsResponse;
import io.github.cristhianm30.heikoh.application.service.TransactionService;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import io.github.cristhianm30.heikoh.domain.exception.RequestValidationException;
import io.github.cristhianm30.heikoh.domain.exception.TransactionNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        TransactionsResponse transactionsResponse1 = TransactionsResponse.builder()
                .type("Gasto")
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now())
                .build();
        TransactionsResponse transactionsResponse2 = TransactionsResponse.builder()
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

        when(transactionService.getTransactions(anyLong(), any(TransactionsRequest.class)))
                .thenReturn(Flux.just(transactionsResponse1, transactionsResponse2));

        StepVerifier.create(transactionHandler.getTransactions(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldHandleQueryParams() {
        TransactionsResponse transactionsResponse = TransactionsResponse.builder()
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

        when(transactionService.getTransactions(anyLong(), any(TransactionsRequest.class)))
                .thenReturn(Flux.just(transactionsResponse));

        StepVerifier.create(transactionHandler.getTransactions(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldFilterByType() {
        TransactionsResponse transactionsResponse = TransactionsResponse.builder()
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

        when(transactionService.getTransactions(anyLong(), any(TransactionsRequest.class)))
                .thenReturn(Flux.just(transactionsResponse));

        StepVerifier.create(transactionHandler.getTransactions(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getTransactions_ShouldReturnBadRequest_WhenValidationFails() {
        // Arrange
        when(serverRequest.queryParam("year")).thenReturn(Optional.of("2023"));
        when(serverRequest.queryParam("month")).thenReturn(Optional.of("1"));
        when(serverRequest.queryParam("limit")).thenReturn(Optional.of("10"));
        when(serverRequest.queryParam("offset")).thenReturn(Optional.of("0"));
        when(serverRequest.queryParam("type")).thenReturn(Optional.empty());
        doThrow(new RequestValidationException("Validation error")).when(validateRequest).validate(any(TransactionsRequest.class));

        // Act & Assert
        StepVerifier.create(transactionHandler.getTransactions(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof RequestValidationException &&
                        throwable.getMessage().equals("Validation error")
                )
                .verify();
    }

    @Test
    void getTransactionDetail_ShouldReturnTransactionResponse() {
        // Arrange
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .date(LocalDate.now())
                .type("Gasto")
                .build();

        when(serverRequest.pathVariable("transactionId")).thenReturn("1");
        when(serverRequest.queryParam("type")).thenReturn(Optional.of("expense"));
        when(transactionService.getTransactionDetail(anyLong(), anyLong(), anyString()))
                .thenReturn(Mono.just(transactionResponse));

        // Act & Assert
        StepVerifier.create(transactionHandler.getTransactionDetail(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getTransactionDetail_ShouldReturnNotFound_WhenTransactionNotFound() {
        // Arrange
        when(serverRequest.pathVariable("transactionId")).thenReturn("1");
        when(serverRequest.queryParam("type")).thenReturn(Optional.of("expense"));
        when(transactionService.getTransactionDetail(anyLong(), anyLong(), anyString()))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(transactionHandler.getTransactionDetail(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.NOT_FOUND))
                .verifyComplete();
    }

    @Test
    void getTransactionDetail_ShouldReturnBadRequest_WhenInvalidType() {
        // Arrange
        when(serverRequest.pathVariable("transactionId")).thenReturn("1");
        when(serverRequest.queryParam("type")).thenReturn(Optional.of("invalid"));
        when(transactionService.getTransactionDetail(anyLong(), anyLong(), anyString()))
                .thenReturn(Mono.error(new TransactionNotFoundException("Invalid transaction type")));

        // Act & Assert
        StepVerifier.create(transactionHandler.getTransactionDetail(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof TransactionNotFoundException &&
                        throwable.getMessage().equals("Invalid transaction type")
                )
                .verify();
    }
}

