package net.bons.commptes.integration;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import net.bons.commptes.Cotize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Module(
    injects = Cotize.class,
    library = true
)
public class VertxModule {
  private static final Logger LOG = LoggerFactory.getLogger(VertxModule.class);
  @Provides
  Vertx provideVertx() {
    return Vertx.vertx();
  }

  @Provides
  Router provideRouter(Vertx vertx, StaticHandler staticHandler, CreateProject createProject) {
    Router router = Router.router(vertx);

    router.get("/*").handler(staticHandler);

    router.route().handler(BodyHandler.create());

    router.post("/api/project").handler(createProject);

    router.post("/api/*").handler(event -> {
      HttpServerResponse response = event.response();
      response.putHeader("content-type", "application/json");
      response.end(event.<String>get("body"));
    });

    return router;
  }

  @Provides
  StaticHandler provideStaticHandler() {
    StaticHandler staticHandler = StaticHandler.create("public");
    staticHandler.setIndexPage("index.html");
    return staticHandler;
  }

  @Provides
  CreateProject provideCreateProject(MongoClient mongoClient) {
    return new CreateProject(mongoClient);
  }

  @Provides
  MongoClient provideMongoClient(Vertx vertx) {
    JsonObject config = new JsonObject();
    return MongoClient.createShared(vertx, config);
  }
}
