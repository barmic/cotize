package net.bons.comptes.cqrs.utils;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import net.bons.comptes.cqrs.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;

public class CommandExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(CommandExtractor.class);
    private Validator validator;

    @Inject
    public CommandExtractor(Validator validator) {
        this.validator = validator;
    }

    public boolean validCmd(Command command) throws ValidationError {
        Collection<ConstraintViolation<Command>> constraintViolations = validator.validate(command);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nb errors {}", constraintViolations.size());
            constraintViolations.forEach(
                    violation -> LOG.debug("Violation : {} (field : {}; value {})", violation.getMessage(),
                                           violation.getPropertyPath(), violation.getInvalidValue()));
        }
        if (!constraintViolations.isEmpty()) {
            throw new ValidationError("Erreur du message reçus", constraintViolations);
        }
        return true;
    }
}
