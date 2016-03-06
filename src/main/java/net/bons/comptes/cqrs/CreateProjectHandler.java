package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.service.MailService;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CreateProjectHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private EventBus eventBus;
    private MongoClient mongoClient;
    private CommandExtractor commandExtractor;
    private MailService mailService;

    @Inject
    public CreateProjectHandler(EventBus eventBus, MongoClient mongoClient, CommandExtractor commandExtractor,
                                MailService mailService) {
        this.eventBus = eventBus;
        this.mongoClient = mongoClient;
        this.commandExtractor = commandExtractor;
        this.mailService = mailService;
    }

    @Override
    public void handle(RoutingContext event) {
        rx.Observable.just(event)
                     .map(RoutingContext::getBodyAsJson)
                     .filter(jsonCommand -> {
                         CreateProject createProject = new CreateProject(jsonCommand);
                         return commandExtractor.validCmd(createProject);
                     })
                     .map(project -> project.put("identifier", createId())
                                            .put("passAdmin", createId()))
                     .flatMap(project -> mongoClient.saveObservable("CotizeEvents", project)
                                                    .doOnError(throwable -> LOG.error("Error during query on mongodb", throwable))
                                                    .map(id -> Tuple.of(id, project)))
                     .map(project -> {
                         mailService.sendCreatedProject(new RawProject(project._2));
                         return project;
                     })
                     .subscribe(tuple2 -> {
                         event.response()
                              .putHeader("Content-Type", "application/json")
                              .end(tuple2._2.toString());
                     }, Utils.manageError(event));
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
