package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.cqrs.utils.CommandExtractor;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.MailService;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreateProjectHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private static final int NB_ID_GEN = 10;
    private static final int SIZE_ID = 10;
    private final String cotizeEvents = "CotizeEvents";
    private MongoClient mongoClient;
    private CommandExtractor commandExtractor;
    private MailService mailService;

    @Inject
    public CreateProjectHandler(MongoClient mongoClient, CommandExtractor commandExtractor, MailService mailService) {
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

    private rx.Observable<JsonObject> fillProject(JsonObject project, String fieldName) {
        Collection<String> potentialIds = createIds();
        JsonObject query = createQuery(fieldName, potentialIds);
        return mongoClient.findObservable(cotizeEvents, query)
                          .map(docs -> project.put(fieldName, findUnusedId(docs, fieldName, potentialIds)));
    }

    private String findUnusedId(Collection<JsonObject> docs, String fieldName, Collection<String> potentialIds) {
        Collection<String> idsAlreadyExisting = docs.stream()
                                                    .map(doc -> doc.getString(fieldName))
                                                    .collect(Collectors.toSet());
        potentialIds.removeAll(idsAlreadyExisting);
        if (potentialIds.isEmpty()) {
            // TODO throw exception (see #32)
            throw new RuntimeException("Impossible to create id for " + fieldName);
        }
        return potentialIds.iterator().next();
    }

    private JsonObject createQuery(String fieldName, Collection<String> potentialIds) {
        JsonArray array = new JsonArray();
        potentialIds.stream().map(id -> new JsonObject().put(fieldName, id)).forEach(array::add);
        JsonObject query = new JsonObject();
        query.put("$or", array);
        return query;
    }

    private Collection<String> createIds() {
        Collection<String> tryIds;
        tryIds = IntStream.range(0, NB_ID_GEN)
                          .mapToObj(i -> UUID.randomUUID().toString().substring(0, SIZE_ID))
                          .collect(Collectors.toList());
        LOG.info("Try {}", tryIds);
        return tryIds;
    }
}
