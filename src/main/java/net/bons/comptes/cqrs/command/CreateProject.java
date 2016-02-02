package net.bons.comptes.cqrs.command;

import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.cqrs.event.ProjectCreated;
import net.bons.comptes.service.model.DecisionProjectionProject;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class CreateProject implements Command {
  @NotNull
  private String name;
  @NotNull
  private String author;
  @NotNull
  private String description;
  @NotNull
  private String mail;

  public CreateProject() {
  }

  public CreateProject(String name, String author, String description, String mail) {
    this.name = name;
    this.author = author;
    this.description = description;
    this.mail = mail;
  }

  public String getName() {
    return name;
  }

  public CreateProject setName(String name) {
    this.name = name;
    return this;
  }

  public String getAuthor() {
    return author;
  }

  public CreateProject setAuthor(String author) {
    this.author = author;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public CreateProject setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getMail() {
    return mail;
  }

  public CreateProject setMail(String mail) {
    this.mail = mail;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateProject that = (CreateProject) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(author, that.author) &&
        Objects.equals(description, that.description) &&
        Objects.equals(mail, that.mail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, author, description, mail);
  }

  @Override
  public Event apply(DecisionProjectionProject project) {
    return new ProjectCreated(UUID.randomUUID().toString(), name, author, description, mail,
                              System.currentTimeMillis());
  }
}
