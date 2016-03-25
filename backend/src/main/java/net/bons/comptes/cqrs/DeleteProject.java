package net.bons.comptes.cqrs;

import com.google.inject.name.Named;
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
    private String projectCollection;

    @Inject
    public DeleteProject(MongoClient mongoClient, @Named("ProjectCollectionName") String projectCollection) {
        this.mongoClient = mongoClient;
        this.projectCollection = projectCollection;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        final String projectId = routingContext.request().getParam("projectId");

        JsonObject query = new JsonObject().put("identifier", projectId);

        mongoClient.removeOneObservable(projectCollection, query)
                   .subscribe(obj -> {
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end();
                   }, Utils.manageError(routingContext));
    }
}
