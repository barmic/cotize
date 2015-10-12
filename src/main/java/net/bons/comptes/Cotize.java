package net.bons.comptes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import net.bons.comptes.integration.DaggerCotizeComponent;
import io.vertx.ext.web.Router;
import net.bons.comptes.integration.CotizeComponent;
import net.bons.comptes.integration.VertxModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cotize extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(Cotize.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    JsonObject config = new JsonObject().put("http.port", getPort());
    DeploymentOptions options = new DeploymentOptions().setConfig(config);

    vertx.deployVerticle(Cotize.class.getName(), options);
  }

  @Override
  public void start() {
    CotizeComponent cotizeComponent = DaggerCotizeComponent.builder()
                                                           .vertxModule(new VertxModule(vertx))
                                                           .build();

    Router router = cotizeComponent.router();

    vertx.createHttpServer()
         .requestHandler(router::accept)
         .listen(config().getInteger("http.port"));
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
