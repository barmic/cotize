package net.bons.comptes.cqrs.command;

import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.model.RawProject;

public interface Command {

  Event apply(RawProject project);

  default String getProjectId() {
    return null;
  }
}
