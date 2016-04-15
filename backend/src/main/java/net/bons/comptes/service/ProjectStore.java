package net.bons.comptes.service;

import com.google.inject.Inject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.integration.MongoConfig;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class ProjectStore {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectStore.class);
    private final MongoClient mongoClient;
    private final MongoConfig mongoConfig;
    private static final int NB_ID_GEN = 10;
    private static final int SIZE_ID = 10;

    @Inject
    public ProjectStore(MongoClient mongoClient, MongoConfig mongoConfig) {
        this.mongoClient = mongoClient;
        this.mongoConfig = mongoConfig;
    }

    public Observable<Void> updateProject(RawProject project) {
        final JsonObject query = new JsonObject().put("identifier", project.getIdentifier());
        return mongoClient.replaceObservable(mongoConfig.getProjectCollection(), query, project.toJson());
    }

    public Observable<RawProject> loadProject(String projectId) {
        LOG.debug("Search projectId {}", projectId);
        final JsonObject query = new JsonObject().put("identifier", projectId);
        return mongoClient.findOneObservable(mongoConfig.getProjectCollection(), query, null).map(RawProject::new);
    }

    public Observable<RawProject> loadProject(String projectId, String adminPass) {
        JsonObject query = new JsonObject().put("identifier", projectId);
        if (adminPass != null && !adminPass.isEmpty()) {
            query.put("passAdmin", adminPass);
        }
        LOG.debug("Search projectId {}", projectId);
        return mongoClient.findOneObservable(mongoConfig.getProjectCollection(), query, null).map(RawProject::new);
    }

    public Observable<RawProject> loadProjects() {
        return mongoClient.findObservable(mongoConfig.getProjectCollection(), new JsonObject())
                          .flatMap(Observable::from)
                          .map(RawProject::new);
    }

    public Observable<Void> removeProject(String projectId) {
        final JsonObject query = new JsonObject().put("identifier", projectId);
        return mongoClient.removeOneObservable(mongoConfig.getProjectCollection(), query);
    }

    public Observable<RawProject> storeProject(CreateProject project) {
        return Observable.just(project.toJson())
                         .flatMap(jsonProject -> fillProject(jsonProject, "identifier"))
                         .flatMap(jsonProject -> fillProject(jsonProject, "passAdmin"))
                         .map(RawProject::new)
                         .flatMap(jsonProject ->
                             mongoClient.saveObservable(mongoConfig.getProjectCollection(), jsonProject.toJson())
                                        .map(o -> jsonProject)
                         );
    }

    private rx.Observable<JsonObject> fillProject(JsonObject project, String fieldName) {
        Collection<String> potentialIds = createIds();
        JsonObject query = createQuery(fieldName, potentialIds);
        return mongoClient.findObservable(mongoConfig.getProjectCollection(), query)
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
