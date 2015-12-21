package net.bons.comptes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import net.bons.comptes.cqrs.Domain;
import net.bons.comptes.cqrs.query.LoadProject;
import net.bons.comptes.integration.CotizeComponent;
import net.bons.comptes.integration.VertxModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.bons.comptes.integration.DaggerCotizeComponent;

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
    Domain domain = cotizeComponent.domain();
    LoadProject loader = cotizeComponent.loadProject();

    vertx.eventBus().consumer("command", domain::recieveCommand);
    vertx.eventBus().consumer("load.project", loader::loadProject);

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
