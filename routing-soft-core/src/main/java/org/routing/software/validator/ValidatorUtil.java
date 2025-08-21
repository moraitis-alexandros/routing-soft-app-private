package org.routing.software.validator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Produces;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Checks for syntax errors in dto:
 * ie the dto must comply with the rules defined by jakarta validator.
 */
@ApplicationScoped
public class ValidatorUtil {

    @Inject
    private Validator validator;
    //TODO LOGGER

    public <T> List<String> validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        List<String> errors = new ArrayList<>();
        if (!violations.isEmpty()) {
            for (ConstraintViolation<T> violation : violations) {
                errors.add(violation.getMessage());
            }
        }
        return errors;
    }
}
