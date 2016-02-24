package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.service.model.AdminProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PayedContribution implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(PayedContribution.class);
    private MongoClient mongoClient;
    private CommandExtractor commandExtractor;

    @Inject
    public PayedContribution(MongoClient mongoClient, CommandExtractor commandExtractor) {
        this.mongoClient = mongoClient;
        this.commandExtractor = commandExtractor;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String projectId = routingContext.request().getParam("projectId");
        String contribId = routingContext.request().getParam("contributionId");


        LOG.debug("Search projectId {}, contributionId {}", projectId, contribId);
        JsonObject query = new JsonObject().put("identifier", projectId);

        mongoClient.findOneObservable("CotizeEvents", query, null)
                   .map(projectJson -> togglePayed(new RawProject(projectJson), contribId))
                   .flatMap(project -> mongoClient.replaceObservable("CotizeEvents", query, project.toJson())
                                                  .map(Void -> new AdminProject(project)))
                   .subscribe(project -> {
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(project.toJson().toString());
                   });
    }

    private RawProject togglePayed(RawProject rawProject, String contribId) {
        Optional<Contribution> contribution = rawProject.getContributions().stream()
                                                        .filter(contrib -> contrib.getContributionId()
                                                                                  .equals(contribId))
                                                        .findFirst();
        Contribution contribution1 = contribution.get();
        contribution1.setPayed(!contribution1.getPayed());
        return rawProject;
    }
}
