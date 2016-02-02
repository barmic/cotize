package net.bons.comptes.cqrs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.collection.Seq;
import net.bons.comptes.cqrs.command.Command;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.service.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Type;
import java.util.UUID;

public class CreateProjectHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private final Type type = new TypeToken<CreateProject>() {}.getType();
    private Gson gson = new Gson();
    private EventBus eventBus;
    private MongoClient mongoClient;
    private Validator validator;

    @Inject
    public CreateProjectHandler(EventBus eventBus, MongoClient mongoClient, Validator validator) {
        this.eventBus = eventBus;
        this.mongoClient = mongoClient;
        this.validator = validator;
    }

    @Override
    public void handle(RoutingContext event) {
        rx.Observable.just(event)
                .map(RoutingContext::getBodyAsJson)
                .map(jsonCommand -> gson.<CreateProject>fromJson(jsonCommand.toString(), type))
                .filter(this::validCmd)
                .map(gson::toJson)
                .map(JsonObject::new)
                .map(project -> {
                    project.put("identifier", createId());
                    project.put("passAdmin", createId());
                    return project;
                })
                .map(Project::new)
                .flatMap(project -> mongoClient.saveObservable("CotizeEvents", project.toJson())
                        .map(id -> Tuple.of(id, project)))
                .subscribe(tuple2 -> {
                    event.response()
                            .putHeader("Content-Type", "application/json")
                            .end(tuple2._2.toJson().toString());
                });

    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    private boolean validCmd(Command command) {
        Seq<ConstraintViolation<Command>> constraintViolations = Seq.ofAll(validator.validate(command));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nb errors {}", constraintViolations.length());
            constraintViolations.map(ConstraintViolation::getMessage).forEach(violation -> LOG.debug("Violation : {}", violation));
        }
        return constraintViolations.isEmpty();
    }
}
