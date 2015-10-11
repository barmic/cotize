package net.bons.comptes.cqrs.query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import net.bons.comptes.cqrs.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class GetProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);

  @FunctionalInterface
  private interface Compute {
    JsonObject apply(JsonObject current, JsonObject event);
  }

  private EnumMap<Event, Compute> computers = new EnumMap<>(Event.class);

  private MongoClient mongoClient;

  @Inject
  public GetProject(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
    computers.put(Event.CREATE, this::computeCreateEvent);
  }

  @Override
  public void handle(RoutingContext routingContext) {
    LOG.info("Get Project");
    String projectId = routingContext.request().getParam("projectId");
    String adminPass = routingContext.request().getParam("adminPass");

    JsonObject query = new JsonObject().put("projectId", projectId);

    mongoClient.find("CotizeEvents", query, res -> {
      if (res.succeeded()) {
        routingContext.put("body", computeProject(res).toString());
      }
      else {
        LOG.error(res.cause().getLocalizedMessage());
        routingContext.put("body", "");
      }
      LOG.info("Next !");
      routingContext.next();
    });

    LOG.info("projectId : {}; adminPass : {}", projectId, adminPass);
  }

  private JsonObject computeProject(AsyncResult<List<JsonObject>> res) {
    JsonObject project = new JsonObject();
    for (JsonObject event : res.result()) {
      Event type = Event.valueOf(event.getString("type"));
      project = computers.get(type).apply(project, event);
    }
    LOG.info("Result {}", project.toString());
    return project;
  }

  /**
   * For CREATE event we keep the fields : "name", "author", "description", "mail", "date", "admin", "projectId"
   * and set the amont to 0
   * @param current not used (can be null)
   * @param createEvent data of CREATE event
   * @return the project result of this creation
   */
  private JsonObject computeCreateEvent(JsonObject current, JsonObject createEvent) {
    Collection<String> fields = Arrays.asList("name", "author", "description", "mail", "date", "admin", "projectId");
    Map<String, Object> datas = createEvent.stream()
                                     .filter(entry -> fields.contains(entry.getKey()))
                                     .collect(Collectors.toMap(
                                         Map.Entry<String, Object>::getKey,
                                         Map.Entry<String, Object>::getValue
                                     ));
    JsonObject project = new JsonObject(datas);
    project.put("amount", 0);
    return project;
  }
}
