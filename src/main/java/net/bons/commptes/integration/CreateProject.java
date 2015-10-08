package net.bons.commptes.integration;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;

public class CreateProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CreateProject.class);
  private final MongoClient mongoClient;
  private final Random random;

  public CreateProject(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
    this.random = new Random();
  }

  @Override
  public void handle(RoutingContext event) {
    JsonObject project = valideAndNormalize(event.getBodyAsJson());
    LOG.debug("New project {}", project);

    mongoClient.save("CotizeEvents", project, event1 -> {
      if (event1.failed()) {
        Throwable cause = event1.cause();
        LOG.error("Failed to store project : {} ({})", cause.getLocalizedMessage(), cause.getClass().toString());
      } else {
        LOG.info("Save success : {}", event1.result());
      }
      event.put("body", project.toString());
      event.next();
    });
  }

  private JsonObject valideAndNormalize(JsonObject bodyAsJson) {
    JsonObject project = new JsonObject();
    project.put("name", bodyAsJson.getString("name"));
    project.put("author", bodyAsJson.getString("author"));
    project.put("description", bodyAsJson.getString("description"));
    project.put("mail", bodyAsJson.getString("mail"));
    project.put("date", new Date().getTime());
    project.put("admin", generateRandomString(12));
    project.put("projectId", generateRandomString(12));
    project.put("type", Event.CREATE);
    return project;
  }

  private String generateRandomString(int length) {
    return random.ints(48,122)
                 .filter(i-> (i<57 || i>65) && (i <90 || i>97))
                 .mapToObj(i -> (char) i)
                 .limit(length)
                 .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                 .toString();
  }
}
