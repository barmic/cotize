package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.integration.MongoConfig;
import net.bons.comptes.service.MailService;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Remind implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(PayedContribution.class);
    private MongoClient mongoClient;
    private MailService mailService;
    private final String projectCollection;

    @Inject
    public Remind(MongoClient mongoClient, MailService mailService,
                  MongoConfig projectCollection) {
        this.mongoClient = mongoClient;
        this.mailService = mailService;
        this.projectCollection = projectCollection.getProjectCollection();
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String projectId = routingContext.request().getParam("projectId");
        String contribId = routingContext.request().getParam("contributionId");


        LOG.debug("Search projectId {}, contributionId {}", projectId, contribId);
        JsonObject query = new JsonObject().put("identifier", projectId);

        mongoClient.findOneObservable(projectCollection, query, null)
                   .map(RawProject::new)
                   .map(project -> Tuple.of(project, getContrib(project, contribId)))
                   .subscribe(tuple -> mailService.sendRelance(tuple._1, tuple._2,
                                                               result -> {
                                                                   if (result.failed()) {
                                                                       Utils.manageError(routingContext, 500).call(result.cause());
                                                                   } else {
                                                                       routingContext.response().end();
                                                                   }
                                                               })
                              ,
                              Utils.manageError(routingContext));
    }

    private Contribution getContrib(RawProject project, String contribId) {
        Optional<Contribution> contribution = project.getContributions()
                                                     .stream()
                                                     .filter(contrib -> contrib.getContributionId().equals(contribId))
                                                     .findFirst();
        if (contribution.isPresent()) {
            Contribution contribution1 = contribution.get();
            if (contribution1.getPayed()) {
                new RuntimeException("Impossible de relancer une contribution déjà payée");
            }
            return contribution1;
        }
        throw new RuntimeException("Impossible to find contribution " + contribId + " in project " + project.getName());
    }
}
