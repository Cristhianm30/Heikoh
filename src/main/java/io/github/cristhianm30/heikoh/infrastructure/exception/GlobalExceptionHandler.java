package io.github.cristhianm30.heikoh.infrastructure.exception;

import io.github.cristhianm30.heikoh.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.UNEXPECTED_ERROR_TRY_AGAIN;
import static io.github.cristhianm30.heikoh.domain.util.constant.LogConstant.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        log.error(UNEXPECTED_ERROR, ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exceptionName(ex.getClass().getSimpleName())
                .errorMessage(UNEXPECTED_ERROR_TRY_AGAIN)
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserAlreadyExistsException(UserAlreadyExistsException ex, ServerWebExchange exchange) {
        log.warn(USER_REGISTRATION_FAILED, ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exceptionName(ex.getClass().getSimpleName())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidPasswordException(InvalidPasswordException ex, ServerWebExchange exchange) {
        log.warn(USER_LOGIN_FAILED, ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exceptionName(ex.getClass().getSimpleName())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFoundException(UserNotFoundException ex, ServerWebExchange exchange) {
        log.warn(FIND_USER_FAILED, ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exceptionName(ex.getClass().getSimpleName())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(UserNotEnabledException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotEnabledException(UserNotEnabledException ex, ServerWebExchange exchange) {
        log.warn(USER_NOT_ENABLED, ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exceptionName(ex.getClass().getSimpleName())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler(RequestValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequestException(RequestValidationException ex, ServerWebExchange exchange) {
        log.warn(BAD_REQUEST_HANDLER, ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exceptionName(ex.getClass().getSimpleName())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

}