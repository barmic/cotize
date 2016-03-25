package net.bons.comptes.cqrs;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class ListProject implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ListProject.class);
    private MongoClient mongoClient;

    @Inject
    public ListProject(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        mongoClient.findObservable("CotizeEvents", new JsonObject())
                   .map(jsonObjects -> jsonObjects.stream()
                                                  .map(RawProject::new)
                                                  .collect(Collectors.toList()))
                   .subscribe(obj -> {
                       JsonArray array = new JsonArray();
                       obj.stream().map(RawProject::toJson).forEach(array::add);
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(array.toString());
                   }, Utils.manageError(routingContext));
    }
}
