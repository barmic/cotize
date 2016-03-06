package net.bons.comptes.integration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailConfig;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mail.MailClient;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.serviceproxy.ProxyHelper;
import net.bons.comptes.cqrs.*;
import net.bons.comptes.service.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;

public class VertxModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(VertxModule.class);

    private final Vertx vertx;
    private final JsonObject config;

    public VertxModule(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(vertx.eventBus());

        io.vertx.core.Vertx delegate = (io.vertx.core.Vertx) vertx.getDelegate();
        bind(io.vertx.core.Vertx.class).toInstance(delegate);
        bind(Vertx.class).toInstance(vertx);
        bind(JsonObject.class).toInstance(config);

        EventStore service = ProxyHelper.createProxy(EventStore.class, delegate, "database-service-address");
        bind(EventStore.class).toInstance(service);
    }

    @Provides
    Router provideRouter(Vertx vertx, StaticHandler staticHandler, GetProject getProject,
                         ContrubutionUpdateHandler contrubutionUpdateHandler, ProjectAgreggate projectAgreggate,
                         CreateProjectHandler createProjectHandler, ContributionHandler contributionHandler,
                         GetContribution getContribution, DeleteContribution deleteContribution,
                         PayedContribution payedContribution, Remind remind) {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        // command
        router.post("/api/project").handler(createProjectHandler);
        router.post("/api/project/:projectId/contribution").handler(contributionHandler);
        router.post("/api/project/:projectId/contribution/:contributionId").handler(
                contrubutionUpdateHandler); // update contribution
        router.delete("/api/project/:projectId/contribution/:contributionId").handler(deleteContribution);
        router.post("/api/project/:projectId/contribution/:contributionId/payed").handler(payedContribution);
        router.post("/api/project/:projectId/contribution/:contributionId/remind").handler(remind);

        // query
        router.get("/api/project/:projectId").handler(getProject);
        router.get("/api/project/:projectId/admin/:adminPass").handler(getProject);
        router.get("/api/project/:projectId/contribution/:contributionId").handler(getContribution);

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
        JsonObject mongo = config.getJsonObject("mongo");
        JsonObject host = new JsonObject()
                .put("host", mongo.getString("host"))
                .put("port", mongo.getInteger("port"));
        JsonObject config = new JsonObject()
                .put("db_name", mongo.getString("dbname"))
                .put("useObjectId", true)
                .put("hosts", new JsonArray().add(host))
                .put("username", System.getenv("MONGO_USER"))
                .put("password", System.getenv("MONGO_PASSWD"));

        MongoClient mongoClient = MongoClient.createShared(vertx, config);
        mongoClient.createCollectionObservable(mongo.getString("collection"))
                   .subscribe(aVoid -> {
                       LOG.info("Created ok!");
                   }, throwable -> {
                       LOG.warn("Can't create the collection {}", throwable.getMessage());
                   });
        return mongoClient;
    }

    @Provides
    @Singleton
    MailClient provideMailClient() {
        JsonObject mailConfig = this.config.getJsonObject("mail");
        MailConfig config = new MailConfig().setHostname(mailConfig.getString("host"))
                                            .setSsl(true)
                                            .setPort(mailConfig.getInteger("port"))
                                            .setUsername(mailConfig.getString("user"))
                                            .setPassword(mailConfig.getString("password"));

        return MailClient.createShared(vertx, config);
    }

    @Provides
    @Singleton
    Validator provideValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
