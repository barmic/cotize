package net.bons.commptes;

import dagger.ObjectGraph;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import net.bons.commptes.integration.Configuration;
import net.bons.commptes.integration.VertxModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cotize extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(Cotize.class);

  private Router router;
  private Vertx vertx;
  private Configuration configuration;

  public Cotize(Vertx vertx, Router router, Configuration configuration) {
    this.router = router;
    this.vertx = vertx;
    this.configuration = configuration;
  }

  public static void main(String[] args) {
    ObjectGraph objectGraph = ObjectGraph.create(new VertxModule());
    Cotize cotize = objectGraph.get(Cotize.class);

    cotize.start();
  }

  public void start() {
    vertx.createHttpServer()
         .requestHandler(router::accept)
         .listen(configuration.getPort());
  }
}
