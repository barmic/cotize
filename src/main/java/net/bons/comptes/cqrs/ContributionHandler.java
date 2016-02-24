package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.Tuple2;
import net.bons.comptes.cqrs.command.ContributeProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

public class ContributionHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private MongoClient mongoClient;
    private CommandExtractor commandExtractor;

    @Inject
    public ContributionHandler(MongoClient mongoClient, CommandExtractor commandExtractor) {
        this.mongoClient = mongoClient;
        this.commandExtractor = commandExtractor;
    }

    @Override
    public void handle(RoutingContext event) {
        String projectId = event.request().getParam("projectId");
        LOG.debug("Search projectId {}", projectId);
        JsonObject query = new JsonObject().put("identifier", projectId);

        rx.Observable.just(event)
                     .map(RoutingContext::getBodyAsJson)
                     .map(ContributeProject::new)
                     .filter(commandExtractor::validCmd)
                     .flatMap(cmd -> mongoClient.findOneObservable("CotizeEvents", query, null)
                                                .map(projectJson -> Tuple.of(new RawProject(projectJson), cmd)))
                     .map(tuple -> compute(tuple._1, tuple._2))
                     .flatMap(project -> mongoClient.replaceObservable("CotizeEvents", query, project._1.toJson())
                                                    .map(Void -> project))
                     .map(project -> project._2)
                     .subscribe(project -> {
                         event.response()
                              .putHeader("Content-Type", "application/json")
                              .end(project.toJson().toString());
                     });
    }

    private Tuple2<RawProject, Contribution> compute(RawProject project, ContributeProject contribute) {
        LOG.debug("RawProject to contribute : {}", project.toJson());
        boolean present = project.getContributions()
                                 .stream()
                                 .filter(deal -> Objects.equals(deal.getAuthor(), contribute.getAuthor()))
                                 .findFirst()
                                 .isPresent();
        RawProject.Builder rawProjectBuilder = RawProject.builder(project);
        Contribution contribution = null;
        if (!present) {
            contribution = new Contribution(createId(), contribute.getAuthor(), contribute.getAmount(),
                                                         contribute.getMail());
            rawProjectBuilder.addContribution(contribution);
        } // TODO throw error if a contribution already exists
        return Tuple.of(rawProjectBuilder.createRawProject(), contribution);
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}