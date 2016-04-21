package net.bons.comptes.service.model;

import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
class Utils {
    public static  <T extends JsonModel> Collection<T> extractArray(JsonObject json, String label, Function<JsonObject, T> factory) {
    if (json.containsKey(label)) {
        return json.getJsonArray(label).stream().map(o -> (JsonObject) o).map(factory).collect(Collectors.toList());
    } else {
        return Collections.emptyList();
    }
}
}
