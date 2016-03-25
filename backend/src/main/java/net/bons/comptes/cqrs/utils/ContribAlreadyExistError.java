package net.bons.comptes.cqrs.utils;

public class ContribAlreadyExistError extends RuntimeException {
    public ContribAlreadyExistError(String message) {
        super(message);
    }
}
