package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.AdminProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PayedContribution implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(PayedContribution.class);
    private ProjectStore projectStore;

    @Inject
    public PayedContribution(ProjectStore projectStore) {
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String projectId = routingContext.request().getParam("projectId");
        String contribId = routingContext.request().getParam("contributionId");

        projectStore.loadProject(projectId)
                   .map(project -> togglePayed(project, contribId))
                   .flatMap(project -> projectStore.updateProject(project)
                                                   .map(Void -> new AdminProject(project)))
                   .map(project -> foundUpdatedContribution(contribId, project))
                   .subscribe(contribution -> {
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(contribution.toJson().toString());
                   }, Utils.manageError(routingContext));
    }

    private Contribution foundUpdatedContribution(String contribId, AdminProject project) {
        return project.getContributions()
                      .stream()
                      .filter(c -> c.getContributionId().equals(contribId))
                      .findFirst()
                      .get();
    }

    private RawProject togglePayed(RawProject rawProject, String contribId) {
        Optional<Contribution> contribution = rawProject.getContributions().stream()
                                                        .filter(contrib -> contrib.getContributionId()
                                                                                  .equals(contribId))
                                                        .findFirst();
        if (!contribution.isPresent()) {
            throw new RuntimeException("Impossible to find the contribution " + contribId);
        }
        Contribution contribution1 = contribution.get();
        contribution1.setPayed(!contribution1.getPayed());
        return rawProject;
    }
}
