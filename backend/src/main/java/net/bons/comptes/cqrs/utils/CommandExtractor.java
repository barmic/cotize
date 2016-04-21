package net.bons.comptes.cqrs.utils;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.function.Function;

public class CommandExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(CommandExtractor.class);
    private Validator validator;

    @Inject
    public CommandExtractor(Validator validator) {
        this.validator = validator;
    }

    public <T extends Command> Observable<T> readQuery(RoutingContext context, Function<JsonObject, T> factory) {
        try {
            final T value = factory.apply(context.getBodyAsJson());
            validCmd(value);
            return rx.Observable.just(value);
        } catch (Exception error) {
            return rx.Observable.error(error);
        }
    }

    private boolean validCmd(Command command) throws ValidationEexception {
        Collection<ConstraintViolation<Command>> constraintViolations = validator.validate(command);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nb errors {}", constraintViolations.size());
            constraintViolations.forEach(
                    violation -> LOG.debug("Violation : {} (field : {}; value {})", violation.getMessage(),
                                           violation.getPropertyPath(), violation.getInvalidValue()));
        }
        if (!constraintViolations.isEmpty()) {
            throw new ValidationEexception("Erreur du message reçus", constraintViolations);
        }
        return true;
    }
}
