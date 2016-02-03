package net.bons.comptes.cqrs;

import com.google.gson.Gson;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.bons.comptes.cqrs.command.Command;
import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.model.DecisionProjectionProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ProjectAgreggate implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAgreggate.class);
    private Gson gson = new Gson();
    private EventBus eventBus;

    @Inject
    public ProjectAgreggate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void handle(RoutingContext event) {
        rx.Observable.just(event.<Command>get("cmd"))
                     .subscribe(cmd -> {
                         DecisionProjectionProject projectionProject = event.get("decisionProjection");
                         Event event1 = cmd.apply(projectionProject);
                         eventBus.publish("cmd", event1);
                         event.response()
                              .putHeader("Content-Type", "application/json")
                              .end(gson.toJson(event1));
                     });
    }
}
