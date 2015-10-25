package net.bons.comptes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import net.bons.comptes.cqrs.command.StoreEvent;
import net.bons.comptes.integration.DaggerCotizeComponent;
import io.vertx.ext.web.Router;
import net.bons.comptes.integration.CotizeComponent;
import net.bons.comptes.integration.VertxModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Scanner;

public class Cotize extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(Cotize.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
//    JsonObject internalConfig = loadInternalConfig("cotize.json");
//    JsonObject userConfig = loadUserConfig("/home/michel/cotize.json");//System.getProperty("config"));
//    JsonObject config = new JsonObject().put("user", userConfig)
//                                        .put("internal", internalConfig);
//    DeploymentOptions options = new DeploymentOptions().setConfig(config);

    vertx.deployVerticle(Cotize.class.getName());
  }

  @Override
  public void start() {
    CotizeComponent cotizeComponent = DaggerCotizeComponent.builder()
                                                           .vertxModule(new VertxModule(vertx, config()))
                                                           .build();

    Router router = cotizeComponent.router();
    StoreEvent storeEvent = cotizeComponent.storeEvent();

    vertx.eventBus().consumer("command.project", storeEvent::insertProject);

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
