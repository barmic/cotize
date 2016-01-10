package net.bons.comptes.cqrs.event;

import net.bons.comptes.service.model.Project;

import javax.validation.constraints.NotNull;

public class ProjectCreated implements Event {
  @NotNull
  private String projectId;
  @NotNull
  private String name;
  @NotNull
  private String author;
  @NotNull
  private String description;
  @NotNull
  private String mail;
  @NotNull
  private long creation;
  @NotNull
  private String eventType = "CREATE";

  public ProjectCreated(String projectId, String name, String author, String description, String mail) {
    this(projectId ,name, author, description, mail, System.currentTimeMillis());
  }

  public ProjectCreated(String projectId,
                        String name,
                        String author,
                        String description,
                        String mail,
                        long creation) {
    this.projectId = projectId;
    this.name = name;
    this.author = author;
    this.description = description;
    this.mail = mail;
    this.creation = creation;
  }

  public String getName() {
    return name;
  }

  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public String getMail() {
    return mail;
  }

  @Override
  public String getProjectId() {
    return projectId;
  }

  @Override
  public long getCreation() {
    return creation;
  }

  public String getEventType() {
    return eventType;
  }

  @Override
  public Project apply(Project project) {
    return null;
  }
}
