package net.bons.comptes.service.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class DecisionProjectionProject {
    public DecisionProjectionProject(DecisionProjectionProject project) {
    }

    public DecisionProjectionProject() {
    }

    public DecisionProjectionProject(JsonObject json) {
    }

    public JsonObject toJson() {
        return new JsonObject();
    }
}
