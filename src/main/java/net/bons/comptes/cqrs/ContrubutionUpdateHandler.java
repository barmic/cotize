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

import java.util.Optional;

public class ContrubutionUpdateHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private MongoClient mongoClient;
    private CommandExtractor commandExtractor;

    @Inject
    public ContrubutionUpdateHandler(MongoClient mongoClient, CommandExtractor commandExtractor) {
        this.mongoClient = mongoClient;
        this.commandExtractor = commandExtractor;
    }

    @Override
    public void handle(RoutingContext event) {
        String projectId = event.request().getParam("projectId");
        String contribId = event.request().getParam("contributionId");

        LOG.debug("Search projectId {}, contributionId {}", projectId, contribId);
        JsonObject query = new JsonObject().put("identifier", projectId);

        rx.Observable.just(event)
                     .map(RoutingContext::getBodyAsJson)
                     .map(ContributeProject::new)
                     .filter(commandExtractor::validCmd)
                     .flatMap(cmd -> mongoClient.findOneObservable("CotizeEvents", query, null)
                                                .map(projectJson -> Tuple.of(new RawProject(projectJson), cmd)))
                     .map(tuple -> updateContrib(tuple._1, tuple._2))
                     .flatMap(project -> mongoClient.replaceObservable("CotizeEvents", query, project._1.toJson())
                                                    .map(Void -> project))
                     .subscribe(project -> {
                         event.response()
                              .putHeader("Content-Type", "application/json")
                              .end(project._2.toJson().toString());
                     });

    }

    private Tuple2<RawProject, Contribution> updateContrib(RawProject project, ContributeProject contribution) {
        Optional<Contribution> deal1 = project.getContributions()
                                              .stream()
                                              .filter(d -> d.getAuthor().equals(contribution.getAuthor()))
                                              .findFirst();
        LOG.debug("Author {}", contribution.getAuthor());
        if (deal1.isPresent()) {
            deal1.get().setAmount(contribution.getAmount());
            LOG.debug("Projet result {}", project.toJson());
        }
        return Tuple.of(project, deal1.get());
    }
}
