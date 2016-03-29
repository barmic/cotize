package net.bons.comptes.cqrs.command;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.model.DecisionProjectionProject;

public interface Command {

  Event apply(DecisionProjectionProject decisionProjectionProject);

  default String getProjectId() {
    return null;
  }
}
