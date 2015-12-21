package net.bons.comptes.cqrs.command;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.impl.MessageImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.mockito.Matchers;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StoreEventTest {

  public static class MessageJsonObject extends MessageImpl<String, JsonObject> { }

  @Test
  public void testInsertProject() throws Exception {
    // given
    String mongoCollection = "myCollection";
    MongoClient mongoclient = mock(MongoClient.class);

    // to collect the results
    List<String> resultCollection = new ArrayList<>(1);
    List<JsonObject> resultJsonObject = new ArrayList<>(1);

    when(mongoclient.save(any(String.class), any(JsonObject.class), Matchers.<Handler<AsyncResult<String>>>any()))
        .then(invocation -> {
          resultCollection.add(invocation.getArgumentAt(0, String.class));
          resultJsonObject.add(invocation.getArgumentAt(1, JsonObject.class));
          return mongoclient;
        });

    StoreEvent storeEvent = new StoreEvent(mongoclient, mongoCollection);
    JsonObject inputJsonObject = new JsonObject().put("k1", "v1");
    Message<JsonObject> message = createMockMessage(inputJsonObject);

    // when
    storeEvent.contributeProject(message);

    // then
    assertThat(resultCollection).hasSize(1);
    assertThat(resultCollection.get(0)).isEqualTo(mongoCollection);

    assertThat(resultJsonObject).hasSize(1);
    assertThat(resultJsonObject.get(0)).isEqualTo(inputJsonObject);
  }

  private Message<JsonObject> createMockMessage(JsonObject inputJsonObject) {
    Message<JsonObject> message = mock(MessageJsonObject.class);
    when(message.body()).thenReturn(inputJsonObject);
    return message;
  }

  @Test
  public void testContributeProject() throws Exception {

  }
}