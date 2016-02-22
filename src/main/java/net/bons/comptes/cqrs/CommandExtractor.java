package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import net.bons.comptes.cqrs.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;

public class CommandExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private Validator validator;

    @Inject
    public CommandExtractor(Validator validator) {
        this.validator = validator;
    }

    public boolean validCmd(Command command) {
        Collection<ConstraintViolation<Command>> constraintViolations = validator.validate(command);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nb errors {}", constraintViolations.size());
            constraintViolations.forEach(
                    violation -> LOG.debug("Violation : {} (field : {}; value {})", violation.getMessage(),
                                           violation.getPropertyPath(), violation.getInvalidValue()));
        }
        return constraintViolations.isEmpty();
    }
}
