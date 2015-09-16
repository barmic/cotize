package net.bons.commptes.integration;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);

  @Override
  public void handle(RoutingContext routingContext) {
    String projectId = routingContext.request().getParam("projectId");
    String adminPass = routingContext.request().getParam("adminPass");

    LOG.info("projectId : {}; adminPass : {}", projectId, adminPass);
  }
}
