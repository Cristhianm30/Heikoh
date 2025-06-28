package io.github.cristhianm30.heikoh.infrastructure.infrastructure.exception;

import io.github.cristhianm30.heikoh.domain.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.UNEXPECTED_ERROR_TRY_AGAIN;
import static io.github.cristhianm30.heikoh.domain.util.constant.LogConstant.UNEXPECTED_ERROR;
import static io.github.cristhianm30.heikoh.domain.util.constant.LogConstant.USER_REGISTRATION_FAILED;

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

}