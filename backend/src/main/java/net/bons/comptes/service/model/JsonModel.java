package net.bons.comptes.service.model;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.json.JsonObject;

public interface JsonModel {
    JsonObject toJson();
}
