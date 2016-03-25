package net.bons.comptes.cqrs;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DeleteProject implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ListProject.class);
    private MongoClient mongoClient;

    @Inject
    public DeleteProject(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        final String projectId = routingContext.request().getParam("projectId");

        JsonObject query = new JsonObject().put("identifier", projectId);

        mongoClient.removeOneObservable("CotizeEvents", query)
                   .subscribe(obj -> {
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end();
                   }, Utils.manageError(routingContext));
    }
}
