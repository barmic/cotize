package net.bons.comptes.cqrs.command;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.cqrs.event.ProjectCreated;
import net.bons.comptes.service.model.DecisionProjectionProject;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@DataObject(generateConverter = true, inheritConverter = true)
public class CreateProject implements Command {
    @NotNull @NotBlank
    private String name;
    @NotNull @NotBlank
    private String author;
    @NotNull @NotBlank
    private String description;
    @NotNull @Email @NotBlank
    private String mail;

    public CreateProject() {
    }

    public CreateProject(String name, String author, String description, String mail) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.mail = mail;
    }

    public CreateProject(CreateProject createProject) {
        this.name = createProject.getName();
        this.author = createProject.getAuthor();
        this.description = createProject.getDescription();
        this.mail = createProject.getMail();
    }

    public CreateProject(JsonObject json) {
        this.name = json.getString("name");
        this.author = json.getString("author");
        this.description = json.getString("description");
        this.mail = json.getString("mail");
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("name", this.name)
                .put("author", this.author)
                .put("description", this.description)
                .put("mail", this.mail);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
