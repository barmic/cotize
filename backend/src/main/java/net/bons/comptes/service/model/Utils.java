package net.bons.comptes.service.model;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vertx.core.json.JsonObject;

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
