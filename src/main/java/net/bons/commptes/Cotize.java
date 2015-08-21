package net.bons.commptes;

import com.google.common.collect.Lists;
import dagger.ObjectGraph;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import net.bons.commptes.integration.Configuration;
import net.bons.commptes.integration.CotizeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    ObjectGraph objectGraph = ObjectGraph.create(new CotizeModule());
    Cotize cotize = objectGraph.get(Cotize.class);

    cotize.start();
  }

  public void start() {
    vertx.createHttpServer()
         .requestHandler(router::accept)
         .listen(configuration.getPort());
  }
}
