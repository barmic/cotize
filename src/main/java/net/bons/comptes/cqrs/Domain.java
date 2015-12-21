package net.bons.comptes.cqrs;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import net.bons.comptes.cqrs.command.Command;

import javax.inject.Inject;
import java.util.Random;

public class Domain {
  private static final Logger LOG = LoggerFactory.getLogger(Domain.class);
  private final Random random = new Random();
  private MongoClient mongoClient;
  private String collectionName;
  private EventBus eventBus;

  @Inject
  public Domain(MongoClient mongoClient, String collectionName, EventBus eventBus) {
    this.mongoClient = mongoClient;
    this.collectionName = collectionName;
    this.eventBus = eventBus;
  }

  public void recieveCommand(Message<JsonObject> message) {
    JsonObject command = message.body();
    LOG.debug("Received command : {}", command.toString());

//    Command commandType = Command.getFromName(command.getString(Command.COMMAND_TYPE_FIELD));
//
//    switch (commandType) {
//      case CREATE:
//        JsonObject createEvent = new JsonObject(command.getMap()).put("projectId", generateRandomString(6))//
//                                                                 .put("admin", generateRandomString(6));
//        mongoClient.insert(collectionName, createEvent, event -> {
//          message.reply(createEvent);
//        });
//        break;
//      case CONTRIBUTE:
//        JsonObject contributeEvent = new JsonObject(command.getMap()).put("projectId", generateRandomString(6))//
//                                                                 .put("admin", generateRandomString(6));
//        mongoClient.insert(collectionName, contributeEvent, event -> {
//          message.reply(contributeEvent);
//        });
//        break;
//    }
  }

  protected String generateRandomString(int length) {
    return random.ints(48,122)
                 .filter(i-> (i<57 || i>65) && (i <90 || i>97))
                 .map(i -> i)
                 .limit(length)
                 .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                 .toString();
  }
}
