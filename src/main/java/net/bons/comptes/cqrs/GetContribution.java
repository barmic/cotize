package net.bons.comptes.cqrs;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.collection.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Function;

public class GetContribution implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);
    private static final Set<String> publicFields = HashSet.of("amount", "author", "date", "name", "identifier",
                                                               "description", "deals");
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
                .map(obj -> filter(obj, contributionId))
                .subscribe(obj -> {
                    routingContext.response().putHeader("Content-Type", "application/json").end(obj.toString());
                });
    }

    JsonObject filter(JsonObject project, String contributionId) {
        Map<String, Object> collect = project.stream()
                .filter(entry -> publicFields.contains(entry.getKey()))
                .map(entry -> Tuple.of(entry.getKey(), entry.getValue()))
                .collect(HashMap.collector());
        JsonArray deals = (JsonArray) collect.get("deals").getOrElse(new JsonArray());
        JsonArray filteredDeals = new JsonArray();
        deals.stream()
                .map(o -> (JsonObject) o)
                .filter(deal -> deal.getString("dealId").equals(contributionId))
                .forEach(filteredDeals::add);
        collect = collect.put("deals", filteredDeals);
        return new JsonObject(collect.toJavaMap(Function.identity()));
    }
}
