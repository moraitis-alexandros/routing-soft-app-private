package org.routing.software.validator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ValidatorProducer {

    private final Validator validator;

    public ValidatorProducer() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Produces
    public Validator produceValidator() {
        return validator;
    }
}
