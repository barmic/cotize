package net.bons.comptes.cqrs.command;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class ValidateEvent {
  private final Collection<String> requieredFields;

  public ValidateEvent(Collection<String> requieredFields) {
    this.requieredFields = ImmutableSet.copyOf(requieredFields);
  }

  public ValidateEvent(ImmutableCollection<String> requieredFields) {
    this.requieredFields = requieredFields;
  }

  public JsonObject validAndClean(JsonObject event) {
    Map<String, Object> cleanedEvent = event.stream()
                                            .filter(entry -> requieredFields.contains(entry.getKey()))
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    checkArgument(cleanedEvent.size() == requieredFields.size());
    return new JsonObject(cleanedEvent);
  }
}
