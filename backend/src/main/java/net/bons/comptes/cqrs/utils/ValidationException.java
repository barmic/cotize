package net.bons.comptes.cqrs.utils;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import javaslang.collection.List;
import javaslang.collection.Traversable;
import net.bons.comptes.cqrs.command.Command;

import javax.validation.ConstraintViolation;

public class ValidationException extends RuntimeException {
    private Traversable<ConstraintViolation<Command>> violations;

    public ValidationException(String message,
                               Traversable<ConstraintViolation<Command>> violations) {
        super(message);
        this.violations = List.ofAll(violations);
    }

    public Traversable<ConstraintViolation<Command>> getViolations() {
        return violations;
    }
}
