package net.bons.commptes.integration;

import io.vertx.core.json.JsonObject;

public enum Event {
  CREATE {
    @Override
    public JsonObject compute(JsonObject current, JsonObject event) {
      return event;
    }
  };

  public abstract JsonObject compute(JsonObject current, JsonObject event);
}
