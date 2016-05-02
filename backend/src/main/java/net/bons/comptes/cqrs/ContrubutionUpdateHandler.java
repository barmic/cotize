package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.control.Option;
import net.bons.comptes.cqrs.command.ContributeProject;
import net.bons.comptes.cqrs.utils.CommandExtractor;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        commandExtractor.readQuery(event, ContributeProject::new)
                        .flatMap(cmd -> projectStore.loadProject(projectId)
                                                    .map(projectJson -> Tuple.of(projectJson, cmd)))
                        .map(tuple -> updateContrib(tuple._1, tuple._2))
                        .flatMap(project -> projectStore.updateProject(project._1)
                                                        .map(voided -> project))
                        .subscribe(project ->
                            event.response()
                                 .putHeader("Content-Type", "application/json")
                                 .end(project._2.toJson().toString())
                        , Utils.manageError(event));
    }

    private Tuple2<RawProject, Contribution> updateContrib(RawProject project, ContributeProject contribution) {
        Option<Contribution> deal1 = project.getContributions()
                                            .filter(d -> d.getAuthor().equals(contribution.getAuthor()))
                                            .headOption();
        RawProject.Builder builder = RawProject.builder(project);
        Contribution contrib = deal1.filter(d -> !d.getPayed())
                                    .map(d -> d.setAmount(contribution.getAmount()))
                                    .getOrElseThrow(() -> {
                                        throw new RuntimeException("La contribution " + contribution.getAuthor() + " est introuvable dans le projet " + project.getIdentifier());
                                    });
        return Tuple.of(builder.createRawProject(), contrib);
    }
}
