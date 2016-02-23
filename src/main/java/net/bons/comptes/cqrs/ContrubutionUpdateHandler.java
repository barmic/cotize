package net.bons.comptes.cqrs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import net.bons.comptes.cqrs.command.ContributeProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Optional;

public class ContrubutionUpdateHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private final Type type = new TypeToken<ContributeProject>() {
    }.getType();
    private Gson gson = new Gson();
    private EventBus eventBus;
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
                     .map(jsonCommand -> gson.<ContributeProject>fromJson(jsonCommand.toString(), type))
                     .filter(commandExtractor::validCmd)
                     .flatMap(cmd -> mongoClient.findOneObservable("CotizeEvents", query, null)
                                                .map(projectJson -> Tuple.of(projectJson, cmd)))
                     .map(tuple -> Tuple.of(new RawProject(tuple._1), tuple._2))
                     .map(tuple -> updateContrib(tuple._1, tuple._2))
                     .flatMap(project -> mongoClient.replaceObservable("CotizeEvents", query, project.toJson())
                                                    .map(Void -> project))
                     .subscribe(project -> {
                         event.response()
                              .putHeader("Content-Type", "application/json")
                              .end(project.toJson().toString());
                     });

    }

    private RawProject updateContrib(RawProject project, ContributeProject contribution) {
        Optional<Contribution> deal1 = project.getContributions().stream().filter(
                d -> d.getAuthor().equals(contribution.getAuthor())).findFirst();
        LOG.debug("Author {}", contribution.getAuthor());
        if (deal1.isPresent()) {
            deal1.get().setAmount(contribution.getAmount());
            LOG.debug("Projet result {}", project.toJson());
        }
        return project;
    }
}
