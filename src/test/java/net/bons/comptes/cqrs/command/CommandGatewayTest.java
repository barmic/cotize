package net.bons.comptes.cqrs.command;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandGatewayTest {
//  @Test
  public void test() {
    // given
    RoutingContext routingContext = mock(RoutingContext.class);
    when(routingContext.getBody()).thenReturn(Buffer.buffer());
    JsonObject command = new JsonObject().put("", "");
    when(routingContext.getBodyAsJson()).thenReturn(command);

    EventBus eventBus = mock(EventBus.class);
//    ProjectAgreggate gateway = new ProjectAgreggate(eventBus, validator);
//    when(eventBus)

    // when
//    gateway.handle(routingContext);
  }
}