package net.bons.comptes.cqrs.command;

import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.model.RawProject;

import java.util.Objects;

public class ContributeProject implements Command {
  private String author;
  private String mail;
  private String amount;
  private String projectId;

  public ContributeProject() {
  }

  public ContributeProject(String author, String mail, String amount, String projectId) {
    this.author = author;
    this.mail = mail;
    this.amount = amount;
    this.projectId = projectId;
  }

  public String getAuthor() {
    return author;
  }

  public ContributeProject setAuthor(String author) {
    this.author = author;
    return this;
  }

  public String getMail() {
    return mail;
  }

  public ContributeProject setMail(String mail) {
    this.mail = mail;
    return this;
  }

  public String getAmount() {
    return amount;
  }

  public ContributeProject setAmount(String amount) {
    this.amount = amount;
    return this;
  }

  @Override
  public Event apply(RawProject project) {
    return null;
  }

  public String getProjectId() {
    return projectId;
  }

  public ContributeProject setProjectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ContributeProject that = (ContributeProject) o;
    return Objects.equals(author, that.author) &&
        Objects.equals(mail, that.mail) &&
        Objects.equals(amount, that.amount) &&
        Objects.equals(projectId, that.projectId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(author, mail, amount, projectId);
  }
}
