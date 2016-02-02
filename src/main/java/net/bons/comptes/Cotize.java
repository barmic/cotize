package net.bons.comptes;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.serviceproxy.ProxyHelper;
import net.bons.comptes.integration.VertxModule;
import net.bons.comptes.service.EventStore;
import net.bons.comptes.service.MongoEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cotize extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(Cotize.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(Cotize.class.getName());
  }

  @Override
  public void start() {
    Injector injector = Guice.createInjector(new VertxModule(vertx, config()));

    Router router = injector.getInstance(Router.class);
    MongoEventStore dataStoreService = injector.getInstance(MongoEventStore.class);

    ProxyHelper.registerService(EventStore.class, (io.vertx.core.Vertx) vertx.getDelegate(), dataStoreService,
        "database-service-address");
    // TODO register MongoEventStore
//    MongoEventStore loader = injector.getInstance(MongoEventStore.class);
//    vertx.eventBus().consumer("load.project", loader::loadEvents);

    vertx.createHttpServer()
         .requestHandler(router::accept)
         .listen(getPort());
  }

  /**
   * Get the configuration http port.
   * Retrun the port configured by "PORT" variable environment or "PORT" java property or 5000.
   * @return the port
   */
  public int getPort() {
    Integer port = config().getInteger("http-port");
    if (port == null) {
      String portStr = System.getenv("PORT");
      if (portStr == null) {
        portStr = System.getProperty("PORT", "5000");
      }
      port = Integer.valueOf(portStr);
    }

    LOG.info("Listen on {}", port);
    return port;
  }
}
