package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import javaslang.collection.Seq;
import net.bons.comptes.cqrs.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

public class CommandExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private Validator validator;

    @Inject
    public CommandExtractor(Validator validator) {
        this.validator = validator;
    }

    public boolean validCmd(Command command) {
        Seq<ConstraintViolation<Command>> constraintViolations = Seq.ofAll(validator.validate(command));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nb errors {}", constraintViolations.length());
            constraintViolations.forEach(
                    violation -> LOG.debug("Violation : {} (field : {}; value {})", violation.getMessage(),
                                           violation.getPropertyPath(), violation.getInvalidValue()));
        }
        return constraintViolations.isEmpty();
    }
}
