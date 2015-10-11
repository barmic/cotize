package net.bons.comptes.integration;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import net.bons.comptes.Cotize;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.cqrs.query.GetProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Module(
    injects = Cotize.class
)
public class VertxModule {
  private static final Logger LOG = LoggerFactory.getLogger(VertxModule.class);
  @Provides
  Vertx provideVertx() {
    return Vertx.vertx();
  }

  @Provides
  Router provideRouter(Vertx vertx, StaticHandler staticHandler, CreateProject createProject, GetProject getProject) {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router.post("/api/project").handler(createProject);

    router.get("/api/project/:projectId/admin/:adminPass").handler(getProject);
    router.get("/api/project/:projectId").handler(getProject);

    router.post("/api/project/:projectId/contrib").handler(createProject);

    router.route("/api/*").handler(event -> {
      HttpServerResponse response = event.response();
      response.putHeader("content-type", "application/json");
      response.end(event.<String>get("body"));
    });

    router.get("/*").handler(staticHandler);

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
  GetProject provideGetProject(MongoClient mongoClient) {
    return new GetProject(mongoClient);
  }

  @Provides
  @Singleton
  MongoClient provideMongoClient(Vertx vertx) {
    JsonObject config = new JsonObject();
    config.put("db_name", "bonscomptes");
    config.put("useObjectId", true);

    MongoClient mongoClient = MongoClient.createShared(vertx, config);
    // TODO
    mongoClient.createCollection("CotizeEvents", res -> {
      if (res.succeeded()) {
        LOG.info("Created ok!");
      } else {
        LOG.warn(res.cause().getLocalizedMessage());
      }
    });
    return mongoClient;
  }

  @Provides
  Configuration provideConfiguration() {
    return new Configuration();
  }

  @Provides
  Cotize provideCotize(Configuration configuration, Vertx vertx, Router router) {
    return new Cotize(vertx, router, configuration);
  }
}
