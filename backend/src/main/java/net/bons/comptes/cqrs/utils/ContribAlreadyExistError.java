package net.bons.comptes.cqrs.utils;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

public class ContribAlreadyExistError extends RuntimeException {
    public ContribAlreadyExistError(String message) {
        super(message);
    }
}
