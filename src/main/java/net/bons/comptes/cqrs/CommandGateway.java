package net.bons.comptes.cqrs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import net.bons.comptes.cqrs.command.Command;
import net.bons.comptes.cqrs.command.ContributeProject;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.service.DataStoreService;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Type;

public class CommandGateway implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(CommandGateway.class);
  private Gson gson = new Gson();
  private Map<String, Type> types;
  private Validator validator;
  private DataStoreService dataStoreService;

  private static class Pair {
    private Command command;
    private RawProject project;

    private Pair(Command command, RawProject project) {
      this.command = command;
      this.project = project;
    }

    public Command getCommand() {
      return command;
    }

    public RawProject getProject() {
      return project;
    }
  }

  @Inject
  public CommandGateway(Validator validator, DataStoreService dataStoreService) {
    this.validator = validator;
    this.dataStoreService = dataStoreService;
    this.types = HashMap.ofAll(
        Tuple.of("CREATE", new TypeToken<CreateProject>(){}.getType()),
        Tuple.of("CONTRIBUTE", new TypeToken<ContributeProject>(){}.getType())
    );
  }

  @Override
  public void handle(RoutingContext event) {
    if (event.getBody().length() == 0) {
      event.response().end();
      return;
    }

    rx.Observable.just(event)
                 .map(RoutingContext::getBodyAsJson)
                 .map(this::createCommand)
                 .filter(this::validCmd)
                 .map(cmd -> {
                   dataStoreService.loadProject(cmd.getProjectId(), event1 -> {
                     LOG.info("toto {}", event1.result());
                   });
                   return new Pair(cmd, null);
                 }) // TODO load events & construct project from events
                 .map(pair -> pair.getCommand().apply(pair.getProject()))
                 .map(gson::toJson)
                 .subscribe(cmd -> {
                   event.response().end(cmd);
                 });
  }

  private Command createCommand(JsonObject jsonCommand) {
    String commandType = (String) jsonCommand.remove("commandType");
    return types.get(commandType).map(type1 -> gson.<Command>fromJson(jsonCommand.toString(), type1)).orElse(null);
  }

  // TODO must make an error (error 400) if invalid
  private boolean validCmd(Command command) {
    Seq<ConstraintViolation<Command>> constraintViolations = Seq.ofAll(validator.validate(command));
    if (LOG.isDebugEnabled()) {
      LOG.debug("Nb errors {}", constraintViolations.isEmpty());
      constraintViolations.map(ConstraintViolation::getMessage).forEach(violation -> LOG.debug("Violation : {}", violation));
    }
    return  constraintViolations.isEmpty();
  }
}
