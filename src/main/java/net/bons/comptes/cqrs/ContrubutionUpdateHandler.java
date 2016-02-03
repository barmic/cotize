package net.bons.comptes.cqrs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.command.ContributeProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validator;
import java.lang.reflect.Type;

public class ContrubutionUpdateHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private final Type type = new TypeToken<ContributeProject>() {}.getType();
    private Gson gson = new Gson();
    private EventBus eventBus;
    private MongoClient mongoClient;
    private Validator validator;

    @Inject
    public ContrubutionUpdateHandler(MongoClient mongoClient, Validator validator) {
        this.mongoClient = mongoClient;
        this.validator = validator;
    }

    @Override
    public void handle(RoutingContext event) {
        String projectId = event.request().getParam("projectId");
        String contribId = event.request().getParam("contributionId");

        LOG.debug("Search projectId {}, contributionId {}", projectId, contribId);
        JsonObject query = new JsonObject().put("identifier", projectId);

//        rx.Observable.just(event)
//                .map(RoutingContext::getBodyAsJson)
//                .map(jsonCommand -> gson.<ContributeProject>fromJson(jsonCommand.toString(), type))
//                .filter(this::validCmd)
//                .flatMap(cmd -> mongoClient.findOneObservable("CotizeEvents", query, null)
//                        .map(projectJson -> Tuple.of(projectJson, cmd)))
//                .map(tuple -> Tuple.of(new Project(tuple._1), tuple._2))
//                .map(tuple -> compute(tuple._1, tuple._2))
//                .flatMap(project -> mongoClient.replaceObservable("CotizeEvents", query, project.toJson())
//                        .map(Void -> project))
//                .subscribe(project -> {
//                    event.response()
//                            .putHeader("Content-Type", "application/json")
//                            .end(project.toJson().toString());
//                });

    }
}
