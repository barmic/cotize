package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.AdminProject;
import net.bons.comptes.service.model.JsonModel;
import net.bons.comptes.service.model.RawProject;
import net.bons.comptes.service.model.SimpleProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Function;

public class GetProject implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);
    private final ProjectStore projectStore;

    @Inject
    public GetProject(ProjectStore projectStore) {
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        final String adminPass = routingContext.request().getParam("adminPass");
        final String projectId = routingContext.request().getParam("projectId");
        final String contributionId = routingContext.request().getParam("contributionId");

        LOG.debug("Search projectId {}", projectId);

        Observable<RawProject> sourceProject;

        if (adminPass != null && !adminPass.isEmpty()) {
            sourceProject = projectStore.loadProject(projectId, adminPass);
        } else {
            sourceProject = projectStore.loadProject(projectId);
        }

        Function<RawProject, JsonModel> map = project -> !Objects.equals(project.getPassAdmin(), adminPass) ? new SimpleProject(project)
                                                                                                            : new AdminProject(project);
        if (contributionId != null) {
            map = extractContribution(contributionId);
        }

        sourceProject.map(map::apply)
                     .subscribe(obj ->
                         routingContext.response()
                                       .putHeader("Content-Type", routingContext.getAcceptableContentType())
                                       .end(obj.toJson().toString())
                     , Utils.manageError(routingContext));
    }

    private Function<RawProject, JsonModel> extractContribution(String contributionId) {
        return project -> project.getContributions()
                                 .filter(contrib -> contrib.getContributionId().equals(contributionId))
                                 .headOption()
                                 .getOrElseThrow(() -> new RuntimeException("Impossible de trouver la contribution " + contributionId));
    }
}
