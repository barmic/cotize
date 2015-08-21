package net.bons.commptes.integration;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CreateProject.class);
  private final MongoClient mongoClient;

  public CreateProject(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  @Override
  public void handle(RoutingContext event) {
    JsonObject jsonProject = event.getBodyAsJson();
    LOG.debug("New project {}", jsonProject);

    mongoClient.save("CotizeEvents", jsonProject, event1 -> {
      event.put("body", event1.result());
      event.next();
    });
  }
}
