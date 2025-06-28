package io.github.cristhianm30.heikoh.infrastructure.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String exceptionName;
    private final String errorMessage;
    private final LocalDateTime timestamp;
    private final String path;
}