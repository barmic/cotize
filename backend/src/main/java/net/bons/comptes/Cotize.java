package net.bons.comptes;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.serviceproxy.ProxyHelper;
import net.bons.comptes.integration.VertxModule;
import net.bons.comptes.service.EventStore;
import net.bons.comptes.service.MongoEventStore;

public class Cotize extends AbstractVerticle {

  @Override
  public void start() {
    VertxModule module = new VertxModule(vertx);
    Injector injector = Guice.createInjector(module);

    Router router = injector.getInstance(Router.class);
    MongoEventStore dataStoreService = injector.getInstance(MongoEventStore.class);

    ProxyHelper.registerService(EventStore.class, (io.vertx.core.Vertx) vertx.getDelegate(), dataStoreService,
        "database-service-address");
    // TODO register MongoEventStore
//    MongoEventStore loader = injector.getInstance(MongoEventStore.class);
//    vertx.eventBus().consumer("load.project", loader::loadEvents);

    vertx.createHttpServer()
         .requestHandler(router::accept)
         .listen(module.getPort());
  }
}
