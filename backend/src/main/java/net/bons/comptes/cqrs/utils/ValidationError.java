package net.bons.comptes.cqrs.utils;

import net.bons.comptes.cqrs.command.Command;

import javax.validation.ConstraintViolation;
import java.util.Collection;

public class ValidationError extends RuntimeException {
    private Collection<ConstraintViolation<Command>> violations;

    public ValidationError(String message,
                           Collection<ConstraintViolation<Command>> violations) {
        super(message);
        this.violations = violations;
    }

    public Collection<ConstraintViolation<Command>> getViolations() {
        return violations;
    }
}
