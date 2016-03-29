package net.bons.comptes.cqrs.utils;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

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
