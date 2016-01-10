package net.bons.comptes.cqrs.event;

import net.bons.comptes.service.model.Project;

public interface Event {
  String getProjectId();
  long getCreation();
  Project apply(Project project);
}
