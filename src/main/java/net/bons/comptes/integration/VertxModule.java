package net.bons.comptes.integration;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import net.bons.comptes.cqrs.Domain;
import net.bons.comptes.cqrs.command.CommandGateway;
import net.bons.comptes.cqrs.query.GetProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Module
public class VertxModule {
  private static final Logger LOG = LoggerFactory.getLogger(VertxModule.class);
  private static final String EVENT_COLLECTION_NAME = "CotizeEvents";

  private final Vertx vertx;
  private final JsonObject config;

  public VertxModule(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
  }

  @Provides
  Vertx provideVertx() {
    return vertx;
  }

  // TODO use named inject ?
  @Provides
  Router provideRouter(Vertx vertx, StaticHandler staticHandler, GetProject getProject, CommandGateway commandHanlder) {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    // command
    router.post("/api/command").handler(commandHanlder);
//    router.post("/api/project").handler(createProject);
//    router.route("/api/project/:projectId.*").handler(loadProject);
//    router.post("/api/project/:projectId/contrib").handler(contribute);

    // query
    router.get("/api/project/:projectId/admin/:adminPass").handler(getProject);
    router.get("/api/project/:projectId").handler(getProject);

    router.route("/api/*").handler(event -> {
      HttpServerResponse response = event.response();
      response.putHeader("content-type", "application/json");
      JsonObject body = event.<JsonObject>get("body");
      if (body != null) {
        response.end(body.toString());
      } else {
        response.end();
      }
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
  @Singleton
  MongoClient provideMongoClient(Vertx vertx) {
    JsonObject config = new JsonObject();
    config.put("db_name", "bonscomptes");
    config.put("useObjectId", true);

    MongoClient mongoClient = MongoClient.createShared(vertx, config);
    // TODO
    mongoClient.createCollection(EVENT_COLLECTION_NAME, res -> {
      if (res.succeeded()) {
        LOG.info("Created ok!");
      } else {
        LOG.warn(res.cause().getLocalizedMessage());
      }
    });
    return mongoClient;
  }

  @Provides
  @Singleton
  MailClient provideMailClient() {
    MailConfig config = new MailConfig();
    JsonObject userConfig = this.config.getJsonObject("user").getJsonObject("mail");
    JsonObject mailConfig = this.config.getJsonObject("internal").getJsonObject("mail");
    config.setHostname(userConfig.getString("hostname", mailConfig.getString("hostname")));
    config.setPort(userConfig.getInteger("port", mailConfig.getInteger("port")));
    config.setStarttls(StartTLSOptions.valueOf(userConfig.getString("tls", mailConfig.getString("tls"))));
    config.setUsername(userConfig.getString("username", mailConfig.getString("username")));
    config.setPassword(userConfig.getString("password", mailConfig.getString("password")));
    return MailClient.createNonShared(vertx, config);
  }

  @Provides
  @Singleton
  EventBus provideEventBus(Vertx vertx) {
    return vertx.eventBus();
  }

  @Provides
  @Singleton
  CommandGateway provideCommandGateway(EventBus eventBus, Validator validator) {
    return new CommandGateway(eventBus, validator);
  }

  @Provides
  @Singleton
  Domain provideDomain(MongoClient mongoClient) {
    return new Domain(mongoClient, EVENT_COLLECTION_NAME, vertx.eventBus());
  }

  @Provides
  @Singleton
  Validator provideValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator();
  }
}
