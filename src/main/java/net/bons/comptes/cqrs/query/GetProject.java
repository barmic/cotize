package net.bons.comptes.cqrs.query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import net.bons.comptes.cqrs.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);

  private MongoClient mongoClient;

  public GetProject(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
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
      project = type.compute(project, event);
    }
    LOG.info("Result {}", project.toString());
    return project;
  }
}
