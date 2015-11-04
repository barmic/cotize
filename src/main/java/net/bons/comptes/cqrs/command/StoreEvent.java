package net.bons.comptes.cqrs.command;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

public class StoreEvent {
  private static final Logger LOG = LoggerFactory.getLogger(StoreEvent.class);
  private MongoClient mongoClient;
  private String collection;

  public StoreEvent(MongoClient mongoClient, String collection) {
    this.mongoClient = mongoClient;
    this.collection = collection;
  }

  public void insertProject(Message<JsonObject> messageProject) {
    mongoClient.save(collection, messageProject.body(), result -> {
      if (result.failed()) {
        Throwable cause = result.cause();
        LOG.error("Failed to store project : {} ({})", cause.getLocalizedMessage(), cause.getClass().toString());
        messageProject.fail(1, cause.getMessage());
      } else {
        LOG.info("Save success : {}", result.result());

        messageProject.reply(messageProject.body());
      }
    });
  }

  public void contributeProject(Message<JsonObject> messageProject) {
    mongoClient.save(collection, messageProject.body(), result -> {
      if (result.failed()) {
        Throwable cause = result.cause();
        LOG.error("Failed to store contribution : {} ({})", cause.getLocalizedMessage(), cause.getClass().toString());
        messageProject.fail(1, cause.getMessage());
      } else {
        LOG.info("Save success : {}", result.result());

        messageProject.reply(messageProject.body());
      }
    });
  }
}
