package net.bons.comptes.cqrs;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import net.bons.comptes.cqrs.command.ContributeProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContributionHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private final Type type = new TypeToken<ContributeProject>() {}.getType();
    private Gson gson = new Gson();
    private EventBus eventBus;
    private MongoClient mongoClient;
    public CommandExtractor commandExtractor;

    @Inject
    public ContributionHandler(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void handle(RoutingContext event) {
        String projectId = event.request().getParam("projectId");
        LOG.debug("Search projectId {}", projectId);
        JsonObject query = new JsonObject().put("identifier", projectId);

        rx.Observable.just(event)
                .map(RoutingContext::getBodyAsJson)
                .map(jsonCommand -> gson.<ContributeProject>fromJson(jsonCommand.toString(), type))
                .filter(commandExtractor::validCmd)
                .flatMap(cmd -> mongoClient.findOneObservable("CotizeEvents", query, null)
                        .map(projectJson -> Tuple.of(projectJson, cmd)))
                .map(tuple -> Tuple.of(new Project(tuple._1), tuple._2))
                .map(tuple -> compute(tuple._1, tuple._2))
                .flatMap(project -> mongoClient.replaceObservable("CotizeEvents", query, project.toJson())
                        .map(Void -> project))
                .subscribe(project -> {
                    event.response()
                            .putHeader("Content-Type", "application/json")
                            .end(project.toJson().toString());
                });
    }

    Project compute(Project project, ContributeProject contribute) {
        LOG.debug("Project to contribute : {}", project.toJson());
        boolean present = project.getContributions()
                .stream()
                .filter(deal -> Objects.equals(deal.getAuthor(), contribute.getAuthor()))
                .findFirst()
                .isPresent();
        Project projectResult = project;
        if (!present) {
            Contribution contribution = new Contribution(createId(), contribute.getAuthor(), contribute.getAmount(), contribute.getMail());
            ImmutableList<Contribution> contributions = ImmutableList.<Contribution>builder().addAll(project.getContributions()).add(
                    contribution).build();
            int amount = contributions.stream().mapToInt(d -> d.getAmount()).sum();
            JsonObject jsonObject = project.toJson()
                    .put("amount", amount)
                    .put("contributions", new JsonArray(contributions.stream().map(d -> d.toJson()).collect(Collectors.toList())));
            projectResult = new Project(jsonObject);
            LOG.debug("Projet result {}", projectResult.toJson());
        }
        return projectResult;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
