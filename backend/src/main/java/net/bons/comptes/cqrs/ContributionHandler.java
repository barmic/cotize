package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.Tuple2;
import net.bons.comptes.cqrs.command.ContributeProject;
import net.bons.comptes.cqrs.utils.CommandExtractor;
import net.bons.comptes.cqrs.utils.ContribAlreadyExistError;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.MailService;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

public class ContributionHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ContributionHandler.class);
    private CommandExtractor commandExtractor;
    private MailService mailService;
    private ProjectStore projectStore;

    @Inject
    public ContributionHandler(CommandExtractor commandExtractor, MailService mailService, ProjectStore projectStore) {
        this.commandExtractor = commandExtractor;
        this.mailService = mailService;
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext event) {
        String projectId = event.request().getParam("projectId");

        commandExtractor.readQuery(event, ContributeProject.class)
                        .flatMap(cmd -> projectStore.loadProject(projectId).map(project -> Tuple.of(project, cmd)))
                        .map(this::checkValidNewContribution)
                        .map(this::storeProject)
                        .flatMap(project -> projectStore.updateProject(project._1).map(Void -> project))
                        .subscribe(project -> {
                            mailService.sendNewContribution(project._1, project._2);
                            event.response()
                                 .putHeader("Content-Type", "application/json")
                                 .end(project._2.toJson().toString());
                        }, Utils.manageError(event));
    }

    private Tuple2<RawProject, Contribution> storeProject(Tuple2<RawProject, ContributeProject> tuple) {
        RawProject.Builder rawProjectBuilder = RawProject.builder(tuple._1);
        Contribution contribution = new Contribution(createId(), tuple._2.getAuthor(), tuple._2.getAmount(),
                                                     tuple._2.getMail(), false);
        rawProjectBuilder.addContribution(contribution);
        return Tuple.of(rawProjectBuilder.createRawProject(), contribution);
    }

    private Tuple2<RawProject, ContributeProject> checkValidNewContribution(Tuple2<RawProject, ContributeProject> tuple) {
        LOG.debug("RawProject  to contribute : {}", tuple._1.toJson());
        boolean present = tuple._1.getContributions()
                                 .stream()
                                 .filter(deal -> Objects.equals(deal.getAuthor(), tuple._2.getAuthor()))
                                 .findFirst()
                                 .isPresent();
        if (present) {
            throw new ContribAlreadyExistError("La contribution de " + tuple._2.getAuthor() + " existe déjà");
        }
        return tuple;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
