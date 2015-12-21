package net.bons.comptes.cqrs.command;


import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CommandGateway implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CommandGateway.class);
  private EventBus eventBus;
  private Gson gson = new Gson();
  private Map<String, Type> types;
  private Validator validator;

  @Inject
  public CommandGateway(EventBus eventBus, Validator validator) {
    this.eventBus = eventBus;
    this.validator = validator;
    this.types = ImmutableMap.of(
        "CREATE", new TypeToken<CreateProject>(){}.getType(),
        "CONTRIBUTE", new TypeToken<ContributeProject>(){}.getType()
    );
  }

  @Override
  public void handle(RoutingContext event) {
    if (event.getBody().length() == 0) {
      event.response().end();
      return;
    }
//    rx.Observable.just(event)
//                 .map(RoutingContext::getBodyAsJson)
//                 .map(this::createCommand)
    JsonObject jsonCommand = event.getBodyAsJson();

    Command command = createCommand(jsonCommand);
    Collection<ConstraintViolation<Command>> constraintViolations = validator.validate(command);
    LOG.debug("Nb errors {}", constraintViolations.size());
    constraintViolations.forEach(violation -> LOG.debug("Violation : {}", violation.getMessage()));

//    eventBus.send("command", jsonCommand, res -> {
//      if (res.succeeded()) {
//
//      }
//      event.response().end();
//    });
    event.response().end(command.toString());
  }

  private Command createCommand(JsonObject jsonCommand) {
    Command command = null;
    String commandType = jsonCommand.getString("commandType");
    Type type = types.get(commandType);
    if (type != null) {
      jsonCommand.remove("commandType");
      command = gson.fromJson(jsonCommand.toString(), type);
    }
    return command;
  }
}
