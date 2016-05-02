package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;

import javax.inject.Inject;

public class DeleteProject implements Handler<RoutingContext> {
    private ProjectStore projectStore;

    @Inject
    public DeleteProject(ProjectStore projectStore) {
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        final String projectId = routingContext.request().getParam("projectId");

        projectStore.removeProject(projectId)
                    .subscribe(obj ->
                        routingContext.response()
                                      .putHeader("Content-Type", "application/json")
                                      .end()
                    , Utils.manageError(routingContext));
    }
}
