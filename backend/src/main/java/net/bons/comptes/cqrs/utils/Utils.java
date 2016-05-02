package net.bons.comptes.cqrs.utils;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.functions.Action1;

import javax.validation.ConstraintViolation;
import java.util.stream.Collectors;

public class Utils {

    private Utils() {}

    public static Action1<Throwable> manageError(RoutingContext event) {
        return manageError(event, 400);
    }

    public static Action1<Throwable> manageError(RoutingContext event, int code) {
        return throwable -> {
            JsonArray array = new JsonArray();
            if (throwable instanceof ValidationEexception) {
                ValidationEexception validationEexception = (ValidationEexception) throwable;
                validationEexception.getViolations()
                                    .stream()
                                    .map(ConstraintViolation::getMessage).collect(Collectors.toSet())
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
