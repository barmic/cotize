package net.bons.commptes.integration;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    JsonObject query = new JsonObject().put("_id", projectId);

    mongoClient.findOne("CotizeEvents", query, null, res -> {
      if (res.succeeded()) {
        LOG.info("Result {}", res.result().toString());
        routingContext.put("body", res.result().toString());
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
}
