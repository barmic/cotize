package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import com.google.inject.Inject;
import io.vavr.Tuple;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.MailService;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;

public class Remind implements Handler<RoutingContext> {
    private MailService mailService;
    private final ProjectStore projectStore;

    @Inject
    public Remind(MailService mailService, ProjectStore projectStore) {
        this.mailService = mailService;
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String projectId = routingContext.request().getParam("projectId");
        String contribId = routingContext.request().getParam("contributionId");

        projectStore.loadProject(projectId)
                    .map(project -> Tuple.of(project, getContrib(project, contribId)))
                    .subscribe(tuple -> mailService.sendRelance(tuple._1, tuple._2, routingContext.request().getHeader("host"),
                                                                result -> {
                                                                    if (result.failed()) {
                                                                        Utils.manageError(routingContext, 500)
                                                                             .call(result.cause());
                                                                    } else {
                                                                        routingContext.response().end();
                                                                    }
                                                                })
                            , Utils.manageError(routingContext));
    }

    private Contribution getContrib(RawProject project, String contribId) {
        return project.getContributions()
                      .filter(contrib -> contrib.getContributionId().equals(contribId))
                      .filter(contrib -> !contrib.getPayed())
                      .getOrElseThrow(() -> {
                          throw new RuntimeException("Impossible to find contribution " + contribId + " in project "
                                                             + project.getName());
                      });
    }
}
