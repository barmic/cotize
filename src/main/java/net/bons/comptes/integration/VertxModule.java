package net.bons.comptes.integration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
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
    private static final String EVENT_COLLECTION_NAME = "CotizeEvents";

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

        EventStore service = ProxyHelper.createProxy(EventStore.class, delegate, "database-service-address");
        bind(EventStore.class).toInstance(service);
    }

    @Provides
    Router provideRouter(Vertx vertx, StaticHandler staticHandler, GetProject getProject,
                         LoadProjectDecisionProjection loadProjectDecisionProjection, ProjectAgreggate projectAgreggate,
                         CreateProjectHandler createProjectHandler, ContributionHandler contributionHandler, GetContribution getContribution) {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        // command
        router.post("/api/project").handler(createProjectHandler);
        router.post("/api/project/:projectId/contribution").handler(contributionHandler);
        router.post("/api/project/:projectId/contribution/:contributionId").handler(loadProjectDecisionProjection); // update contribution

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
        JsonObject config = new JsonObject().put("db_name", "bonscomptes")
                .put("useObjectId", true);

        MongoClient mongoClient = MongoClient.createShared(vertx, config);
        mongoClient.createCollectionObservable(EVENT_COLLECTION_NAME)
                .subscribe(aVoid -> {
                    LOG.info("Created ok!");
                }, throwable -> {
                    LOG.error("Can't create the collection {}", throwable.getMessage());
//                 LOG.debug("Can't create the collection", throwable);
                });
        return mongoClient;
    }

    @Provides
    @Singleton
    MailClient provideMailClient() {
        MailConfig config = new MailConfig();
        JsonObject userConfig = this.config.getJsonObject("user").getJsonObject("mail");
        JsonObject mailConfig = this.config.getJsonObject("internal").getJsonObject("mail");

        config.setHostname(userConfig.getString("hostname", mailConfig.getString("hostname")))
                .setPort(userConfig.getInteger("port", mailConfig.getInteger("port")))
                .setStarttls(StartTLSOptions.valueOf(userConfig.getString("tls", mailConfig.getString("tls"))))
                .setUsername(userConfig.getString("username", mailConfig.getString("username")))
                .setPassword(userConfig.getString("password", mailConfig.getString("password")));

        return MailClient.createNonShared(vertx, config);
    }

    @Provides
    @Singleton
    Validator provideValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
