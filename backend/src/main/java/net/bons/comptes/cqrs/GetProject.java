package net.bons.comptes.cqrs;

import com.google.inject.name.Named;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class GetProject implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);
    private final MongoClient mongoClient;
    private final String projectCollection;

    @Inject
    public GetProject(MongoClient mongoClient, @Named("ProjectCollectionName") String projectCollection) {
        this.mongoClient = mongoClient;
        this.projectCollection = projectCollection;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        final String adminPass = routingContext.request().getParam("adminPass");
        final String projectId = routingContext.request().getParam("projectId");
        final String contributionId = routingContext.request().getParam("contributionId");

        LOG.debug("Search projectId {}", projectId);

        JsonObject query = new JsonObject().put("identifier", projectId);
        if (adminPass != null && !adminPass.isEmpty()) {
            query.put("passAdmin", adminPass);
        }

        Function<RawProject, JsonModel> map = project -> !Objects.equals(project.getPassAdmin(), adminPass) ? new SimpleProject(project)
                                                                                                            : new AdminProject(project);
        if (contributionId != null) {
            map = filter(contributionId);
        }

        mongoClient.findOneObservable(projectCollection, query, null)
                   .map(RawProject::new)
                   .map(map::apply)
                   .subscribe(obj -> {
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(obj.toJson().toString());
                   }, Utils.manageError(routingContext));
    }

    private Function<RawProject, JsonModel> filter(String contributionId) {
        return project -> {
            Optional<Contribution> contribution = project.getContributions().stream()
                                                         .filter(contrib -> contrib.getContributionId()
                                                                                   .equals(contributionId))
                                                         .findFirst();
            return contribution.orElseThrow(
                    () -> new RuntimeException("Impossible de trouver la contribution " + contributionId));
        };
    }
}
