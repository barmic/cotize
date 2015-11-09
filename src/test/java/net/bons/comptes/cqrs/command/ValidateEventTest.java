package net.bons.comptes.cqrs.command;

import com.google.common.collect.ImmutableList;
import io.vertx.core.json.JsonObject;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ValidateEventTest {

  @Test
  public void should_throw_IllegalArgumentException_when_object_have_not_all_fields() throws Exception {
    // given
    ValidateEvent validateEvent = new ValidateEvent(ImmutableList.of("foo", "bar"));
    JsonObject obj = new JsonObject().put("foo", "val");

    // when
    Throwable throwable = catchThrowable(() -> validateEvent.validAndClean(obj));

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void should_ok_when_object_contains_all_reqiered_fields_and_more() throws Exception {
    // given
    ImmutableList<String> requieredFields = ImmutableList.of("foo", "bar");
    ValidateEvent validateEvent = new ValidateEvent(requieredFields);
    JsonObject obj = new JsonObject().put("foo", "val")
                                     .put("bar", "var")
                                     .put("more", "more");

    // when
    JsonObject result = validateEvent.validAndClean(obj);

    // then
    assertThat(result).hasSize(2);
    List<String> resultKeys = result.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    assertThat(resultKeys).containsAll(requieredFields);
  }
}