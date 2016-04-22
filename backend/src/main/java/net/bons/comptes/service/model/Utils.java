package net.bons.comptes.service.model;

import io.vertx.core.json.JsonObject;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.Stream;

import java.util.function.Function;

/**
 *
 */
class Utils {
    public static  <T extends JsonModel> Seq<T> extractArray(JsonObject json, String label, Function<JsonObject, T> factory) {
    if (json.containsKey(label)) {
        return List.ofAll(json.getJsonArray(label)).map(o -> (JsonObject) o).map(factory);
    } else {
        return Stream.empty();
    }
}
}
