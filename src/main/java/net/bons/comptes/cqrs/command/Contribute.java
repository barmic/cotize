package net.bons.comptes.cqrs.command;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import net.bons.comptes.cqrs.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 */
public class Contribute implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CreateProject.class);
  private final Collection<String> fields = ImmutableSet.of("author", "mail", "amount", "projectId");
  private final MongoClient mongoClient;

  @Inject
  public Contribute(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final JsonObject project = routingContext.get("project");
    if (project == null) {
      routingContext.fail(400);
    }
    JsonObject contribution = valideAndNormalize(routingContext.getBodyAsJson(), project.getString("projectId"));

    mongoClient.save("CotizeEvents", contribution, event1 -> {
      if (event1.failed()) {
        Throwable cause = event1.cause();
        LOG.error("Failed to store project : {} ({})", cause.getLocalizedMessage(), cause.getClass().toString());
      } else {
        LOG.info("Save success : {}", event1.result());
      }
      routingContext.put("body", contribution);
      routingContext.next();
    });
  }

  private JsonObject valideAndNormalize(JsonObject bodyAsJson, String projectId) {
    Map<String, Object> map = bodyAsJson.stream()
                                        .filter(entry -> fields.contains(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    JsonObject project = new JsonObject(map);
    project.put("projectId", projectId);
    project.put("date", new Date().getTime());
    project.put("type", Event.CONTRIBUTE);
    return project;
  }
}
