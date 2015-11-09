package net.bons.comptes.cqrs.command;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.bons.comptes.cqrs.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.Random;

public class CreateProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CreateProject.class);
  private final Random random;
  private final EventBus eventBus;
  private ValidateEvent validateEvent;

  @Inject
  public CreateProject(EventBus eventBus) {
    this.eventBus = eventBus;
    this.validateEvent = new ValidateEvent(ImmutableSet.of("name", "author", "description", "mail", "date", "admin",
          "projectId"));
    this.random = new Random();
  }

  @Override
  public void handle(RoutingContext routingContext) {
    JsonObject cleaned = validateEvent.validAndClean(routingContext.getBodyAsJson());
    JsonObject project = normalize(cleaned);
    LOG.debug("New project {}", project);

    eventBus.<JsonObject>send("command.project", project, event1 -> {
      if (event1.succeeded()) {
        routingContext.put("body", event1.result().body());
      }
      routingContext.next();
    });
  }

  private JsonObject normalize(JsonObject event) {
    event.put("date", new Date().getTime());
    event.put("admin", generateRandomString(12));
    event.put("projectId", generateRandomString(12));
    event.put("type", Event.CREATE);
    return event;
  }

  private String generateRandomString(int length) {
    return random.ints(48,122)
                 .filter(i-> (i<57 || i>65) && (i <90 || i>97))
                 .map(i -> i)
                 .limit(length)
                 .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                 .toString();
  }
}
