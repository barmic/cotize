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
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ContrubutionUpdateHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ContrubutionUpdateHandler.class);
    private CommandExtractor commandExtractor;
    private ProjectStore projectStore;

    @Inject
    public ContrubutionUpdateHandler(CommandExtractor commandExtractor, ProjectStore projectStore) {
        this.projectStore = projectStore;
        this.commandExtractor = commandExtractor;
    }

    @Override
    public void handle(RoutingContext event) {
        String projectId = event.request().getParam("projectId");
        String contribId = event.request().getParam("contributionId");

        LOG.debug("Search projectId {}, contributionId {}", projectId, contribId);

        commandExtractor.readQuery(event, ContributeProject.class)
                        .flatMap(cmd -> projectStore.loadProject(projectId)
                                                    .map(projectJson -> Tuple.of(projectJson, cmd)))
                        .map(tuple -> updateContrib(tuple._1, tuple._2))
                        .flatMap(project -> projectStore.updateProject(project._1)
                                                        .map(Void -> project))
                        .subscribe(project -> {
                            event.response()
                                 .putHeader("Content-Type", "application/json")
                                 .end(project._2.toJson().toString());
                        }, Utils.manageError(event));
    }

    private Tuple2<RawProject, Contribution> updateContrib(RawProject project, ContributeProject contribution) {
        Optional<Contribution> deal1 = project.getContributions()
                                              .stream()
                                              .filter(d -> d.getAuthor().equals(contribution.getAuthor()))
                                              .findFirst();
        RawProject.Builder builder = RawProject.builder(project);
        if (deal1.isPresent()) {
            Contribution contribution1 = deal1.get();
            if (contribution1.getPayed()) {
                throw new RuntimeException("Impossible de mettre à jour une contribution déjà payée");
            }
            contribution1.setAmount(contribution.getAmount());
            return Tuple.of(builder.createRawProject(), contribution1);
        }
        throw new RuntimeException("La contribution " + contribution.getAuthor() + " est introuvable dans le projet " + project.getIdentifier());
    }
}
