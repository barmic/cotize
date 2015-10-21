package net.bons.comptes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
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

  private static JsonObject loadUserConfig(String config) {
    JsonObject result = null;
    try {
      Scanner in = new Scanner(Paths.get(config));
      StringBuilder sb = new StringBuilder();
      while (in.hasNextLine()) {
        sb.append(in.nextLine());
      }
      result = new JsonObject(sb.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private static JsonObject loadInternalConfig(String internalConfig) {
    InputStream in = Cotize.class.getClassLoader()
                                 .getResourceAsStream(internalConfig);
    Scanner sc = new Scanner(in);
    StringBuilder sb = new StringBuilder();
    while (sc.hasNextLine()) {
      sb.append(sc.nextLine());
    }
    return new JsonObject(sb.toString());
  }

  @Override
  public void start() {
    CotizeComponent cotizeComponent = DaggerCotizeComponent.builder()
                                                           .vertxModule(new VertxModule(vertx, config()))
                                                           .build();

    Router router = cotizeComponent.router();

    vertx.createHttpServer()
         .requestHandler(router::accept)
         .listen(config().getInteger("http-port", Integer.valueOf(System.getProperty("PORT", "5000"))));
  }

  /**
   * Get the configuration http port.
   * Retrun the port configured by "PORT" variable environment or "PORT" java property or 5000.
   * @return the port
   */
  public static int getPort() {
    String port = System.getenv("PORT");
    if (port == null) {
      port = System.getProperty("PORT", "5000");
    }

    LOG.info("Listen on {}", port);
    return Integer.valueOf(port);
  }
}
