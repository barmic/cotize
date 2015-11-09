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
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 */
public class Contribute implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CreateProject.class);
  private final EventBus eventBus;
  private final ValidateEvent validateEvent;

  @Inject
  public Contribute(EventBus eventBus) {
    this.eventBus = eventBus;
    validateEvent = new ValidateEvent(ImmutableSet.of("author", "mail", "amount", "projectId"));
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final JsonObject project = routingContext.get("project");
    if (project == null) {
      routingContext.fail(404);
      return;
    }
    JsonObject cleaned = validateEvent.validAndClean(routingContext.getBodyAsJson());
    JsonObject contribution = normalize(cleaned, project.getString("projectId"));

    eventBus.<JsonObject>send("command.contribute", contribution, event1 -> {
      if (event1.succeeded()) {
        routingContext.put("body", event1.result().body());
      }
      routingContext.next();
    });
  }

  private JsonObject normalize(JsonObject event, String projectId) {
    event.put("projectId", projectId);
    event.put("date", new Date().getTime());
    event.put("type", Event.CONTRIBUTE);
    return event;
  }
}
