package net.bons.comptes.cqrs;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class GetContribution implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);
    private MongoClient mongoClient;

    @Inject
    public GetContribution(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        final String contributionId = routingContext.request().getParam("contributionId");
        final String projectId = routingContext.request().getParam("projectId");

        LOG.debug("Search projectId {}", projectId);

        JsonObject query = new JsonObject().put("identifier", projectId);

        mongoClient.findOneObservable("CotizeEvents", query, null)
                   .map(RawProject::new)
                   .map(obj -> filter(obj, contributionId))
                   .subscribe(obj -> {
                       routingContext.response().putHeader("Content-Type", "application/json").end(obj.toJson().toString());
                   }, Utils.manageError(routingContext));
    }

    Contribution filter(RawProject project, String contributionId) {
        Optional<Contribution> contribution = project.getContributions().stream()
                                                     .filter(contrib -> contrib.getContributionId()
                                                                               .equals(contributionId))
                                                     .findFirst();
        return contribution.orElseThrow(() -> new RuntimeException("Impossible de trouver la contribution " + contributionId));
    }
}
