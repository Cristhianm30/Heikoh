package io.github.cristhianm30.heikoh.domain.exception;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String message) {
        super(message);
    }
}
