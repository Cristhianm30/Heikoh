package io.github.cristhianm30.heikoh.infrastructure.util.validation;

import io.github.cristhianm30.heikoh.domain.exception.RequestValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static io.github.cristhianm30.heikoh.domain.util.constant.InfrastructureConstant.*;

@Component
@RequiredArgsConstructor
public class ValidateRequest {

    private final Validator validator;

    public <T> void validate(T object) {
        var violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + COLON_SEPARATOR + violation.getMessage())
                    .collect(Collectors.joining(COMMA_SEPARATOR));
            throw new RequestValidationException(VALIDATION_FAILED_PREFIX + errorMessages);
        }
    }
}
