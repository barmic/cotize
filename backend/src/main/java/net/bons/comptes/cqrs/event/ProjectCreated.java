package net.bons.comptes.cqrs.event;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.json.JsonObject;

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

  public ProjectCreated(ProjectCreated project) {
    this.projectId = project.getProjectId();
    this.name = project.getName();
    this.author = project.getAuthor();
    this.description = project.getDescription();
    this.mail = project.getMail();
    this.creation = project.getCreation();
  }

  public ProjectCreated() {
  }

  public ProjectCreated(JsonObject json) {
    this.projectId = json.getString("projectId");
    this.name = json.getString("name");
    this.author = json.getString("author");
    this.description = json.getString("description");
    this.mail = json.getString("mail");
    this.creation = json.getLong("creation");
  }

  public JsonObject toJson() {
    return new JsonObject()
            .put("projectId", projectId)
            .put("name", name)
            .put("author", author)
            .put("description", description)
            .put("mail", mail)
            .put("creation", creation);
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
}
