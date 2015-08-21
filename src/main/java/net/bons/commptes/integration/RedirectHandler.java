package net.bons.commptes.integration;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class RedirectHandler implements Handler<RoutingContext> {
  private String uri;

  private RedirectHandler(String uri) {
    this.uri = uri;
  }

  public static RedirectHandler create(String uri) {
    return new RedirectHandler(uri);
  }

  @Override
  public void handle(RoutingContext event) {
    HttpServerResponse response = event.response();
    response.setStatusCode(302);
    response.putHeader("Location", uri);
    response.end();
  }
}
