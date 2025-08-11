package io.github.cristhianm30.heikoh.domain.exception;

public class UserNotEnabledException extends RuntimeException {
    public UserNotEnabledException(String message) {
        super(message);
    }
}