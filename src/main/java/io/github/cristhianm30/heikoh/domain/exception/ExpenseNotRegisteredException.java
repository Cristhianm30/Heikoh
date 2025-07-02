package io.github.cristhianm30.heikoh.domain.exception;

public class ExpenseNotRegisteredException extends RuntimeException {
    public ExpenseNotRegisteredException(String message) {
        super(message);
    }
}
