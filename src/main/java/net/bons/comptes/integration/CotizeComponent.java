package net.bons.comptes.integration;

import dagger.Component;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import net.bons.comptes.Cotize;
import net.bons.comptes.cqrs.Domain;
import net.bons.comptes.cqrs.command.StoreEvent;
import net.bons.comptes.cqrs.query.LoadProject;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = VertxModule.class
)
public interface CotizeComponent {
  void inject(Cotize cotize);
  Router router();
  Domain domain();
  LoadProject loadProject();
}
