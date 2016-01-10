package net.bons.comptes.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.model.Project;

@ProxyGen
public interface DataStoreService {
  // The service methods
  void loadProject(String projectId, Handler<AsyncResult<JsonObject>> resultHandler);
//  void saveEvent(Event event, Handler<AsyncResult<String>> resultHandler);
}
