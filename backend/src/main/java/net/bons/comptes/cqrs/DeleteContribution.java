package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vavr.collection.Seq;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.AdminProject;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;

public class DeleteContribution implements Handler<RoutingContext> {
    private ProjectStore projectStore;

    @Inject
    public DeleteContribution(ProjectStore projectStore) {
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String projectId = routingContext.request().getParam("projectId");
        String contribId = routingContext.request().getParam("contributionId");

        projectStore.loadProject(projectId)
                    .map(projectJson -> removeContrib(projectJson, contribId))
                    .flatMap(project -> projectStore.updateProject(project)
                                                    .map(voided -> new AdminProject(project)))
                    .subscribe(project ->
                        routingContext.response()
                                      .putHeader("Content-Type", "application/json")
                                      .end(project.toJson().toString())
                    , Utils.manageError(routingContext));
    }

    private RawProject removeContrib(RawProject rawProject, String contribId) {
        Seq<Contribution> contribs = rawProject.getContributions()
                                               .filter(contrib -> !contrib.getContributionId().equals(contribId));
        return RawProject.builder(rawProject).contributions(contribs).createRawProject();
    }
}
