package net.bons.comptes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import net.bons.comptes.integration.DaggerCotizeComponent;
import io.vertx.ext.web.Router;
import net.bons.comptes.integration.Configuration;
import net.bons.comptes.integration.CotizeComponent;
import net.bons.comptes.integration.VertxModule;
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
    CotizeComponent cotizeComponent = DaggerCotizeComponent.builder()
                                                           .vertxModule(new VertxModule(vertx))
                                                           .build();

    Router router = cotizeComponent.router();
    Configuration configuration = cotizeComponent.configuration();

    vertx.createHttpServer()
         .requestHandler(router::accept)
         .listen(configuration.getPort());
  }
}
