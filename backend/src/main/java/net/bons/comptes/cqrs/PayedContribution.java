package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vavr.control.Option;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.AdminProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;

public class PayedContribution implements Handler<RoutingContext> {
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
                                                   .map(voidValue -> new AdminProject(project)))
                   .map(project -> foundUpdatedContribution(contribId, project))
                   .subscribe(contribution ->
                       routingContext.response()
                                     .putHeader("Content-Type", "application/json")
                                     .end(contribution.toJson().toString())
                   , Utils.manageError(routingContext));
    }

    private Contribution foundUpdatedContribution(String contribId, AdminProject project) {
        return project.getContributions()
                      .filter(c -> c.getContributionId().equals(contribId))
                      .head();
    }

    private RawProject togglePayed(RawProject rawProject, String contribId) {
        Option<Contribution> contribution = rawProject.getContributions()
                                                      .filter(contrib -> contrib.getContributionId().equals(contribId))
                                                      .headOption();
        if (contribution.isEmpty()) {
            throw new RuntimeException("Impossible to find the contribution " + contribId);
        }
        contribution.map(c -> c.setPayed(!c.getPayed()))
                    .getOrElseThrow(() -> {
                        throw new RuntimeException("Impossible to find the contribution " + contribId);
                    });
        return rawProject;
    }
}
