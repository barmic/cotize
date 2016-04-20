package net.bons.comptes.service.model;

import io.vertx.core.json.JsonObject;

/**
 *
 */
public class ProjectOptions implements JsonModel {
    private Boolean spam;

    public ProjectOptions(Boolean spam) {
        this.spam = spam;
    }

    public ProjectOptions() {
        this.spam = true;
    }

    public ProjectOptions(ProjectOptions options) {
        this.spam = options.getSpam();
    }

    public ProjectOptions(JsonObject jsonObject) {
        if (jsonObject != null) {
            this.spam = jsonObject.getBoolean("spam", true);
        } else {
            spam = true;
        }
    }

    public Boolean getSpam() {
        return spam;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject().put("spam", spam);
    }
}
