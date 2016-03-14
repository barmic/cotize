package net.bons.comptes.cqrs.command;

import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.model.DecisionProjectionProject;

public interface Command {

  Event apply(DecisionProjectionProject decisionProjectionProject);

  default String getProjectId() {
    return null;
  }
}
