package net.bons.comptes.integration;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

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
import java.util.Optional;

public class VertxModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(VertxModule.class);

    private final Vertx vertx;

    public VertxModule(Vertx vertx) {
        this.vertx = vertx;
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
                         ContrubutionUpdateHandler contrubutionUpdateHandler, CreateProjectHandler createProjectHandler,
                         ContributionHandler contributionHandler, UpdateProject updateProject,
                         DeleteContribution deleteContribution, PayedContribution payedContribution, Remind remind,
                         ListProject listProject, DeleteProject deleteProject, OutgoingHandler outgoingHandler) {
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

        String jsonContentType = "application/json";
        router.post("/api/project/:projectId/admin/:adminPass").produces(jsonContentType).handler(updateProject);
        router.post("/api/project/:projectId/admin/:adminPass/outgoing/del").produces(jsonContentType).handler(outgoingHandler);
        router.post("/api/project/:projectId/admin/:adminPass/outgoing").produces(jsonContentType).handler(outgoingHandler);

        env("ROOT_SECRET").ifPresent(rootSecret -> {
            router.get("/api/admin/" + rootSecret + "/project").handler(listProject);
            router.delete("/api/admin/" + rootSecret + "/project/:projectId").handler(deleteProject);
        });

        // query
        router.get("/api/project/:projectId").produces(jsonContentType).handler(getProject);
        router.get("/api/project/:projectId/admin/:adminPass").produces(jsonContentType).handler(getProject);
        router.get("/api/project/:projectId/contribution/:contributionId").produces(jsonContentType).handler(getProject);

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
    MongoConfig provideMongoConfig() {
        return new MongoConfig(env("collection").orElse("CotizeEvents"));
    }

    @Provides
    @Singleton
    MongoClient provideMongoClient(Vertx vertx) {
        JsonObject host = new JsonObject()
                .put("host", env("MONGO_HOST").orElse("localhost"))
                .put("port", envInt("MONGO_PORT").orElse(27_017));
        JsonObject config = new JsonObject()
                .put("db_name", env("MONGO_DBNAME").orElse("bonscomptes"))
                .put("useObjectId", true)
                .put("hosts", new JsonArray().add(host))
                .put("username", env("MONGO_USER").orElse(""))
                .put("password", env("MONGO_PASSWORD").orElse(""));

        MongoClient mongoClient = MongoClient.createShared(vertx, config);
        mongoClient.createCollectionObservable(env("collection").orElse("CotizeEvents"))
                   .subscribe(aVoid -> LOG.info("Created ok!"),
                              throwable -> LOG.warn("Can't create the collection {}", throwable.getMessage()));
        return mongoClient;
    }

    @Provides
    @Singleton
    MailClient provideMailClient() {
        MailConfig config = new MailConfig().setSsl(true);
        env("MAIL_HOST").ifPresent(config::setHostname);
        envInt("MAIL_PORT").ifPresent(config::setPort);
        env("MAIL_USER").ifPresent(config::setUsername);
        env("MAIL_PASSWORD").ifPresent(config::setPassword);

        return MailClient.createShared(vertx, config);
    }

    @Provides
    @Singleton
    Validator provideValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * Get the configuration http port.
     * Retrun the port configured by "PORT" variable environment or "PORT" java property or 5000.
     * @return the port
     */
    public int getPort() {
        return envInt("PORT").orElse(5_000);
    }

    public static Optional<String> env(String varName) {
        return Optional.ofNullable(System.getenv(varName)).filter(value -> !value.isEmpty());
    }

    private Optional<Integer> envInt(String varName) {
        return Optional.ofNullable(System.getenv(varName))
                       .filter(value -> !value.isEmpty())
                       .map(Integer::parseInt);
    }
}
