package net.bons.comptes.cqrs;

import com.google.inject.name.Named;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.model.JsonModel;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class ListProject implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ListProject.class);
    private final MongoClient mongoClient;
    private final String projectCollection;

    @Inject
    public ListProject(MongoClient mongoClient, @Named("ProjectCollectionName") String projectCollection) {
        this.mongoClient = mongoClient;
        this.projectCollection = projectCollection;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        mongoClient.findObservable(projectCollection, new JsonObject())
                   .map(jsonObjects -> jsonObjects.stream()
                                                  .map(RawProject::new)
                                                  .collect(Collectors.toList()))
                   .subscribe(obj -> {
                       JsonArray array = new JsonArray();
                       obj.stream().map(JsonModel::toJson).forEach(array::add);
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(array.toString());
                   }, Utils.manageError(routingContext));
    }
}
