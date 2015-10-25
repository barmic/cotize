package net.bons.comptes.cqrs.command;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
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
import java.util.Random;
import java.util.stream.Collectors;

public class CreateProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CreateProject.class);
  private final Collection<String> fields = ImmutableSet.of("name", "author", "description", "mail", "date", "admin",
      "projectId");
  private final MongoClient mongoClient;
  private final Random random;
  private final EventBus eventBus;
//  private final MailClient mailClient;

  @Inject
  public CreateProject(MongoClient mongoClient, EventBus eventBus) {
    this.mongoClient = mongoClient;
    this.eventBus = eventBus;
//    this.mailClient = mailClient;
    this.random = new Random();
  }

  @Override
  public void handle(RoutingContext event) {
    JsonObject project = valideAndNormalize(event.getBodyAsJson());
    LOG.debug("New project {}", project);


    eventBus.<JsonObject>send("command.project", project, event1 -> {
      if (event1.succeeded()) {
        event.put("body", event1.result().body());
      }
      event.next();
    });
//    mongoClient.save("CotizeEvents", project, event1 -> {
//      if (event1.failed()) {
//        Throwable cause = event1.cause();
//        LOG.error("Failed to store project : {} ({})", cause.getLocalizedMessage(), cause.getClass().toString());
//        event.fail(cause);
//      } else {
//        LOG.info("Save success : {}", event1.result());
//        event.put("body", project);

//        eventBus.send("create-project", project);
//        MailMessage message = new MailMessage();
//        message.setFrom("michel.barret@gmail.com");
//        message.setTo("yoko.java@gmail.com");
//        message.setText("this is the plain message text");
//        mailClient.sendMail(message, event2 -> {
//          if (event2.succeeded()) {
//            LOG.info(event2.result().toString());
//          } else {
//            LOG.error("", event2.cause());
//          }
//        });
//      }
//    });
  }

  private JsonObject valideAndNormalize(JsonObject bodyAsJson) {
    Map<String, Object> map = bodyAsJson.stream()
                                        .filter(entry -> fields.contains(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    JsonObject project = new JsonObject(map);
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
