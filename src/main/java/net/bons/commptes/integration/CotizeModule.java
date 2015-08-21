package net.bons.commptes.integration;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import net.bons.commptes.Cotize;

@Module(
    injects = Cotize.class,
    includes = {
        VertxModule.class
    }
)
public class CotizeModule {

    @Provides
    Configuration provideConfiguration() {
        return new Configuration();
    }

    @Provides
    Cotize provideCotize(Configuration configuration, Vertx vertx, Router router) {
        return new Cotize(vertx, router, configuration);
    }
}
