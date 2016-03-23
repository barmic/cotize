package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.service.MailService;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreateProjectHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private final String cotizeEvents = "CotizeEvents";
    private EventBus eventBus;
    private MongoClient mongoClient;
    private CommandExtractor commandExtractor;
    private MailService mailService;

    @Inject
    public CreateProjectHandler(EventBus eventBus, MongoClient mongoClient, CommandExtractor commandExtractor,
                                MailService mailService) {
        this.eventBus = eventBus;
        this.mongoClient = mongoClient;
        this.commandExtractor = commandExtractor;
        this.mailService = mailService;
    }

    @Override
    public void handle(RoutingContext event) {
        rx.Observable.just(event)
                .map(RoutingContext::getBodyAsJson)
                .filter(jsonCommand -> {
                    CreateProject createProject = new CreateProject(jsonCommand);
                    return commandExtractor.validCmd(createProject);
                })
                .flatMap(project -> fillProject(project, "identifier"))
                .flatMap(project -> fillProject(project, "passAdmin"))
                .flatMap(project -> mongoClient.saveObservable(cotizeEvents, project)
                        .doOnError(throwable -> LOG.error("Error during query on mongodb", throwable))
                        .map(id -> Tuple.of(id, project)))
                .map(project -> {
                    mailService.sendCreatedProject(new RawProject(project._2));
                    return project;
                })
                .subscribe(tuple2 -> {
                    event.response()
                            .putHeader("Content-Type", "application/json")
                            .end(tuple2._2.toString());
                }, Utils.manageError(event));
    }

    private rx.Observable<? extends JsonObject> fillProject(JsonObject project, String fieldName) {
        Collection<String> potentialIds = createIds();
        JsonArray array = new JsonArray();
        potentialIds.stream().map(id -> new JsonObject().put(fieldName, id)).forEach(array::add);
        JsonObject query = new JsonObject();
        query.put("$or", array);
        return mongoClient.findObservable(cotizeEvents, query)
                .map(docs -> {
                    Collection<String> idsAlreadyExisting = docs.stream()
                            .map(doc -> doc.getString(fieldName))
                            .collect(Collectors.toList());
                    Optional<String> identifier = potentialIds.stream()
                            .filter(id -> !idsAlreadyExisting.contains(id))
                            .findFirst();
                    return project.put(fieldName, identifier.get());
                });
    }

    private Collection<String> createIds() {
        Collection<String> tryIds;
        tryIds = IntStream.range(0, 10)
                .mapToObj(i -> UUID.randomUUID().toString().substring(0, 10))
                .collect(Collectors.toList());
        LOG.info("Try {}", tryIds);
        return tryIds;
    }
}
