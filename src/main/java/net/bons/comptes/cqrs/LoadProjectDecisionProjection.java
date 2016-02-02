package net.bons.comptes.cqrs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import net.bons.comptes.cqrs.command.Command;
import net.bons.comptes.cqrs.command.ContributeProject;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.EventStore;
import net.bons.comptes.service.model.DecisionProjectionProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Type;
import java.util.Optional;

public class LoadProjectDecisionProjection implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(LoadProjectDecisionProjection.class);
    private Gson gson = new Gson();
    private Map<String, Type> types;
    private Validator validator;
    private EventStore eventStore;
    private EventBus eventBus;

    @Inject
    public LoadProjectDecisionProjection(Validator validator, EventStore eventStore) {
        this.validator = validator;
        this.eventStore = eventStore;
        this.types = HashMap.ofEntries(
                Tuple.of("CREATE", new TypeToken<CreateProject>() {}.getType()),
                Tuple.of("CONTRIBUTE", new TypeToken<ContributeProject>() {}.getType())
        );
    }

    @Override
    public void handle(RoutingContext event) {
        Optional<String> projectId = Optional.empty();
        rx.Observable.just(event)
                .map(RoutingContext::getBodyAsJson)
                .map(this::createCommand)
                .filter(this::validCmd)
                .map(cmd -> {
                    ObservableFuture<DecisionProjectionProject> future = RxHelper.observableFuture();
                    eventStore.loadEvents(projectId.orElse(null), future.toHandler());
                    return Tuple.of(cmd, future.toBlocking().first());
                })
                .subscribe(tuple -> {
                    LOG.debug("hello");
                    Event event1 = tuple._1.apply(tuple._2);
                    LOG.debug("salut");
                    eventBus.publish("event", event1);
                    LOG.debug("hehe");
                    event.response().end();
                });
    }

    private Command createCommand(JsonObject jsonCommand) {
        String commandType = (String) jsonCommand.remove("commandType");
        return types.get(commandType)
                .map(type1 -> gson.<Command>fromJson(jsonCommand.toString(), type1))
                .getOrElse((Command) null);
    }

    // TODO must make an error (error 400) if invalid
    private boolean validCmd(Command command) {
        Seq<ConstraintViolation<Command>> constraintViolations = Seq.ofAll(validator.validate(command));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nb errors {}", constraintViolations.length());
            constraintViolations.map(ConstraintViolation::getMessage).forEach(violation -> LOG.debug("Violation : {}", violation));
        }
        return constraintViolations.isEmpty();
    }
}
