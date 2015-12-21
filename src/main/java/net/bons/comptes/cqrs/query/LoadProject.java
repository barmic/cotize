package net.bons.comptes.cqrs.query;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import net.bons.comptes.cqrs.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class LoadProject {
  private static final Logger LOG = LoggerFactory.getLogger(LoadProject.class);
  private static final Collection<String> fieldsCreateEvent = ImmutableSet.of("name", "author", "description", "mail",
      "date", "admin", "projectId", "contributions");
  private static final Collection<String> fieldsContributeEvent = ImmutableSet.of("author", "mail", "date", "amount");

  private MongoClient mongoClient;

  @Inject
  public LoadProject(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
//    computers.put(Command.CREATE, this::computeCreateEvent);
//    computers.put(Command.CONTRIBUTE, this::computeContributeEvent);
  }

  public void loadProject(Message<String> messageProjectId) {
    JsonObject query = new JsonObject().put("projectId", messageProjectId.body());

    mongoClient.find("CotizeEvents", query, res -> {
      if (res.succeeded()) {
        List<JsonObject> results = res.result();
        if (results.isEmpty()) {
          LOG.error(res.cause().getLocalizedMessage());
          messageProjectId.fail(1, res.cause().getLocalizedMessage());
        }
        JsonObject project = load(results);
        messageProjectId.reply(project);
      }
    });
  }

  @FunctionalInterface
  private interface Compute {
    JsonObject apply(JsonObject current, JsonObject event);
  }
  private Map<Command, Compute> computers;// = new EnumMap<>(Command.class);

  public JsonObject load(List<JsonObject> res) {
    JsonObject project = new JsonObject();
    for (JsonObject event : res) {
//      Command type = Command.valueOf(event.getString("type"));
//      project = computers.get(type).apply(project, event);
    }
    LOG.info("Result {}", project.toString());
    return project;
  }

  /**
   * For CREATE event we keep the fields : "name", "author", "description", "mail", "date", "admin", "projectId"
   * and set the amont to 0
   * @param current not used (can be null)
   * @param createEvent data of CREATE event
   * @return the project result of this creation
   */
  private JsonObject computeCreateEvent(JsonObject current, JsonObject createEvent) {
    checkArgument(current.getMap().isEmpty());
    Map<String, Object> datas = collect(fieldsCreateEvent, createEvent);
    JsonObject project = new JsonObject(datas);
    project.put("amount", 0);
    return project;
  }

  /**
   * For CONTRIBUTE event we keep the fieldsCreateEvent : "author", "amount", "mail", "date"
   * and set the amont to 0
   * @param current not used (can be null)
   * @param contributionEvent data of CREATE event
   * @return the project result of this creation
   */
  private JsonObject computeContributeEvent(JsonObject current, JsonObject contributionEvent) {
    Map<String, Object> datas = collect(fieldsContributeEvent, contributionEvent);
    JsonObject project = current.copy();
    JsonArray contributions = project.getJsonArray("contributions", new JsonArray());

    project.put("contributions", contributions.add(new JsonObject(datas)));

    Integer amount = project.getInteger("amount");
    project.put("amount", amount + contributionEvent.getInteger("amount"));
    return project;
  }

  private Map<String, Object> collect(Collection<String> fields, JsonObject createEvent) {
    return createEvent.stream()
                      .filter(entry -> fields.contains(entry.getKey()))
                      .collect(Collectors.toMap(
                          Map.Entry::getKey,
                          Map.Entry::getValue
                      ));
  }
}
