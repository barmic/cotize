package net.bons.comptes.cqrs.command;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 */
@Deprecated
public class Contribute implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CreateProject.class);
  private final EventBus eventBus;

  @Inject
  public Contribute(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void handle(RoutingContext routingContext) {
//    final JsonObject project = routingContext.get("project");
//    if (project == null) {
//      routingContext.fail(404);
//      return;
//    }
//    JsonObject cleaned = Command.CONTRIBUTE.getValidateEvent().validCommand(routingContext.getBodyAsJson());
//    JsonObject contribution = Command.CONTRIBUTE.valid(cleaned);//, project.getString("projectId"));
//
//    eventBus.<JsonObject>send("command.contribute", contribution, event1 -> {
//      if (event1.succeeded()) {
//        routingContext.put("body", event1.result().body());
//      }
//      routingContext.next();
//    });
  }
}
