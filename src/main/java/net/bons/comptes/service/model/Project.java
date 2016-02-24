package net.bons.comptes.service.model;

import io.vertx.core.json.JsonObject;

public interface Project {
    JsonObject toJson();

    String getName();

    String getAuthor();

    String getDescription();

    int getAmount();

    String getMail();

    String getIdentifier();
}
