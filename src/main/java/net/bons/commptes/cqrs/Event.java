package net.bons.commptes.cqrs;

import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

public enum Event {
  CREATE {
    @Override
    public JsonObject compute(JsonObject current, JsonObject event) {
      Collection<String> fields = Arrays.asList("name", "author", "description", "mail", "date", "admin", "projectId");
      Map<String, Object> datas = event.stream()
                                       .filter(entry -> fields.contains(entry.getKey()))
                                       .collect(Collectors.toMap(Map.Entry<String, Object>::getKey, Map.Entry<String, Object>::getValue));
      JsonObject project = new JsonObject(datas);
      project.put("amount", 0);
      return project;
    }
  };

  public abstract JsonObject compute(JsonObject current, JsonObject event);
}
