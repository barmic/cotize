package net.bons.comptes.cqrs;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateRawProjectHandlerTest {

    @Test
    public void testHandle() throws Exception {
        // GIVEN
        CreateProjectHandler createProjectHandler = new CreateProjectHandler(mock(EventBus.class), mock(MongoClient.class), mock(CommandExtractor.class));
        RoutingContext event = mock(RoutingContext.class);
        JsonObject query = new JsonObject("{}");
        when(event.getBodyAsJson()).thenReturn(query);

        // WHEN
        createProjectHandler.handle(event);

        //THEN
//        verify(event).
    }
}