package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.command.CreateProject;
import net.bons.comptes.cqrs.utils.CommandExtractor;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.MailService;
import net.bons.comptes.service.ProjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateProjectHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(CreateProjectHandler.class);
    private CommandExtractor commandExtractor;
    private MailService mailService;
    private ProjectStore projectStore;

    @Inject
    public CreateProjectHandler(CommandExtractor commandExtractor, MailService mailService, ProjectStore projectStore) {
        this.commandExtractor = commandExtractor;
        this.mailService = mailService;
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext event) {
        commandExtractor.readQuery(event, CreateProject.class)
                        .flatMap(project -> projectStore.storeProject(project))
                        .map(project -> {
                            mailService.sendCreatedProject(project);
                            return project;
                        })
                        .subscribe(tuple2 -> {
                            event.response()
                                 .putHeader("Content-Type", "application/json")
                                 .end(tuple2.toJson().toString());
                        }, Utils.manageError(event));
    }
}
