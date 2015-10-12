package net.bons.comptes.cqrs.query;

import io.vertx.core.json.JsonObject;
import net.bons.comptes.cqrs.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class LoadProject {
  private static final Logger LOG = LoggerFactory.getLogger(LoadProject.class);
  private static final Collection<String> fields = Arrays.asList("name", "author", "description", "mail", "date",
      "admin", "projectId");

  @FunctionalInterface
  private interface Compute {
    JsonObject apply(JsonObject current, JsonObject event);
  }
  private Map<Event, Compute> computers = new EnumMap<>(Event.class);

  @Inject
  public LoadProject() {
    computers.put(Event.CREATE, this::computeCreateEvent);
  }

  public JsonObject load(List<JsonObject> res) {
    JsonObject project = new JsonObject();
    for (JsonObject event : res) {
      Event type = Event.valueOf(event.getString("type"));
      project = computers.get(type).apply(project, event);
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
    Map<String, Object> datas = createEvent.stream()
                                           .filter(entry -> fields.contains(entry.getKey()))
                                           .collect(Collectors.toMap(
                                         Map.Entry<String, Object>::getKey,
                                         Map.Entry<String, Object>::getValue
                                     ));
    JsonObject project = new JsonObject(datas);
    project.put("amount", 0);
    return project;
  }
}
