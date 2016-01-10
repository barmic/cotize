package net.bons.comptes.service;

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
import net.bons.comptes.service.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.lang.reflect.Type;

public class MongoStoreService implements DataStoreService {
  private static final Logger LOG = LoggerFactory.getLogger(MongoStoreService.class);
  private Gson gson = new Gson();

  private MongoClient mongoClient;
  private Map<String, Type> types;

  @Inject
  public MongoStoreService(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
    this.types = HashMap.of(
        "CREATE", new TypeToken<ProjectCreated>(){}.getType()
    );
  }

  @Override
  public void loadProject(String projectId, Handler<AsyncResult<JsonObject>> resultHandler) {
    LOG.info("Call loadProject");
    JsonObject query = new JsonObject().put("projectId", projectId);

    mongoClient.findObservable("CotizeEvents", query)
               .flatMap(Observable::from)
               .map(this::createEvent)
               .reduce((Project) null, (project, event) -> event.apply(project))
               .map(gson::toJson)
               .map(JsonObject::new)
               .subscribe(project -> resultHandler.handle(Future.succeededFuture(project)));
  }

//  @Override
  public void saveEvent(Event event, Handler<AsyncResult<String>> resultHandler) {
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
