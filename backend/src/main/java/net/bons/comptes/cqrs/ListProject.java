package net.bons.comptes.cqrs;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.JsonModel;

import javax.inject.Inject;

public class ListProject implements Handler<RoutingContext> {
    private final ProjectStore projectStore;

    @Inject
    public ListProject(ProjectStore projectStore) {
        this.projectStore = projectStore;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        projectStore.loadProjects()
                    .toList()
                    .subscribe(projects -> {
                        JsonArray array = new JsonArray();
                        projects.stream().map(JsonModel::toJson).forEach(array::add);
                        routingContext.response()
                                      .putHeader("Content-Type", "application/json")
                                      .end(array.toString());
                    }, Utils.manageError(routingContext));
    }
}
