package net.bons.comptes.service;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.cqrs.event.ProjectCreated;
import net.bons.comptes.service.model.DecisionProjectionProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Type;

public class MongoEventStore implements EventStore {
    private static final Logger LOG = LoggerFactory.getLogger(MongoEventStore.class);
    private Gson gson = new Gson();

    private MongoClient mongoClient;
    private Map<String, Type> types;

    @Inject
    public MongoEventStore(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.types = HashMap.of(
                "CREATE", new TypeToken<ProjectCreated>(){}.getType()
        );
    }

    @Override
    public void loadEvents(String projectId, Handler<AsyncResult<DecisionProjectionProject>> resultHandler) {
        LOG.info("Call loadEvents");

//    JsonObject query = new JsonObject().put("projectId", projectId);

//    List<Event> events = List.empty();
//    mongoClient.findObservable("CotizeEvents", query)
//            .flatMap(Observable::from)
//            .map(this::createEvent).toBlocking().getIterator().forEachRemaining(events::push);
//    resultHandler.handle(Future.succeededFuture(events.head()));

        resultHandler.handle(Future.succeededFuture(new DecisionProjectionProject()));
    }

    //  @Override
    public void saveEvent(ProjectCreated event, Handler<AsyncResult<String>> resultHandler) {
        rx.Observable.just(event)
                .map(gson::toJson).map(JsonObject::new)
                .flatMap(json -> mongoClient.saveObservable("CotizeEvents", json))
                .subscribe(id -> resultHandler.handle(Future.succeededFuture(id)));
    }

    private Event createEvent(JsonObject jsonEvent) {
        Event command = null;
        String eventTypeField = "eventType";
        String eventType = jsonEvent.getString(eventTypeField);
        Type type = types.apply(eventType);
        if (type != null) {
            jsonEvent.remove(eventTypeField);
            command = gson.fromJson(jsonEvent.toString(), type);
        }
        return command;
    }
}
