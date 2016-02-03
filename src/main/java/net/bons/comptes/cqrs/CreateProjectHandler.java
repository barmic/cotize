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
import net.bons.comptes.cqrs.command.ContributeProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.UUID;

public class CreateProjectHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private final Type type = new TypeToken<ContributeProject>() {}.getType();
    private Gson gson = new Gson();
    private EventBus eventBus;
    private MongoClient mongoClient;
    private CommandExtractor commandExtractor;

    @Inject
    public CreateProjectHandler(EventBus eventBus, MongoClient mongoClient, CommandExtractor commandExtractor) {
        this.eventBus = eventBus;
        this.mongoClient = mongoClient;
        this.commandExtractor = commandExtractor;
    }

    @Override
    public void handle(RoutingContext event) {
        rx.Observable.just(event)
                .map(RoutingContext::getBodyAsJson)
                .map(jsonCommand -> gson.<ContributeProject>fromJson(jsonCommand.toString(), type))
                .filter(commandExtractor::validCmd)
                .map(gson::toJson)
                .map(JsonObject::new)
                .map(project -> project.put("identifier", createId()).put("passAdmin", createId()))
                .flatMap(project -> mongoClient.saveObservable("CotizeEvents", project)
                        .map(id -> Tuple.of(id, project)))
                .subscribe(tuple2 -> {
                    event.response()
                            .putHeader("Content-Type", "application/json")
                            .end(tuple2._2.toString());
                });

    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
