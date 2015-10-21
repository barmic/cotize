package net.bons.comptes.integration;

import dagger.Component;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import net.bons.comptes.Cotize;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = VertxModule.class
)
public interface CotizeComponent {
  void inject(Cotize cotize);
  Router router();
}
