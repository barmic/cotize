package net.bons.comptes.service.model;

import io.vertx.codegen.annotations.VertxGen;

@VertxGen
public interface Project {
  String getName();

  String getAuthor();

  String getDescription();

  int getAmount();
}
