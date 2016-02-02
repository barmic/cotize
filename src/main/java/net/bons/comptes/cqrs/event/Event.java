package net.bons.comptes.cqrs.event;

public interface Event {
    String getProjectId();

    long getCreation();
}
