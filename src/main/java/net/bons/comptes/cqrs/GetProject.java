package net.bons.comptes.cqrs;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.service.model.AdminProject;
import net.bons.comptes.service.model.RawProject;
import net.bons.comptes.service.model.SimpleProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;

public class GetProject implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);
    private MongoClient mongoClient;

    @Inject
    public GetProject(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        final String adminPass = routingContext.request().getParam("adminPass");
        final String projectId = routingContext.request().getParam("projectId");

        LOG.debug("Search projectId {}", projectId);

        JsonObject query = new JsonObject().put("identifier", projectId);
        if (adminPass != null && !adminPass.isEmpty()) {
            query.put("passAdmin", adminPass);
        }

        mongoClient.findOneObservable("CotizeEvents", query, null)
                   .map(RawProject::new)
                   .map(project -> !Objects.equals(project.getPassAdmin(), adminPass) ? new SimpleProject(project)
                                                                                      : new AdminProject(project))
                   .subscribe(obj -> {
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(obj.toJson().toString());
                   }, Utils.manageError(routingContext));
    }
}
