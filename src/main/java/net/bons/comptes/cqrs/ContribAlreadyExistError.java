package net.bons.comptes.cqrs;

public class ContribAlreadyExistError extends RuntimeException {
    public ContribAlreadyExistError(String message) {
        super(message);
    }
}
