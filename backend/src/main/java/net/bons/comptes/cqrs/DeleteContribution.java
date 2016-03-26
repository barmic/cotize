package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.integration.MongoConfig;
import net.bons.comptes.service.model.AdminProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteContribution implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteContribution.class);
    private MongoClient mongoClient;
    private String projectCollection;

    @Inject
    public DeleteContribution(MongoClient mongoClient, MongoConfig projectCollection) {
        this.mongoClient = mongoClient;
        this.projectCollection = projectCollection.getProjectCollection();
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String projectId = routingContext.request().getParam("projectId");
        String contribId = routingContext.request().getParam("contributionId");


        LOG.debug("Search projectId {}, contributionId {}", projectId, contribId);
        JsonObject query = new JsonObject().put("identifier", projectId);

        mongoClient.findOneObservable("CotizeEvents", query, null)
                   .map(projectJson -> removeContrib(new RawProject(projectJson), contribId))
                   .flatMap(project -> mongoClient.replaceObservable(projectCollection, query, project.toJson())
                                                  .map(Void -> new AdminProject(project)))
                   .subscribe(project -> {
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(project.toJson().toString());
                   }, Utils.manageError(routingContext));
    }

    private RawProject removeContrib(RawProject rawProject, String contribId) {
        List<Contribution> contributions = rawProject.getContributions().stream()
                                               .filter(contrib -> !contrib.getContributionId().equals(contribId))
                                               .collect(Collectors.toList());
        return RawProject.builder(rawProject).contributions(contributions).createRawProject();
    }
}
