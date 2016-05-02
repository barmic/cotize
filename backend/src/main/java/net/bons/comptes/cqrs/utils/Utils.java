package net.bons.comptes.cqrs.utils;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.functions.Action1;

import javax.validation.ConstraintViolation;

public class Utils {

    private Utils() {}

    public static Action1<Throwable> manageError(RoutingContext event) {
        return manageError(event, 400);
    }

    public static Action1<Throwable> manageError(RoutingContext event, int code) {
        return throwable -> {
            JsonArray array = new JsonArray();
            if (throwable instanceof ValidationException) {
                ValidationException validationException = (ValidationException) throwable;
                validationException.getViolations()
                                   .map(ConstraintViolation::getMessage)
                                   .forEach(array::add);
            } else {
                array.add(throwable.getMessage());
            }
            event.response()
                 .setStatusCode(code)
                 .putHeader("Content-Type", "application/json")
                 .end(array.toString());
        };
    }
}
