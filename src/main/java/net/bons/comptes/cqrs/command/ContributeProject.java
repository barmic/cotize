package net.bons.comptes.cqrs.command;

import io.vertx.core.json.JsonObject;
import net.bons.comptes.cqrs.event.Event;
import net.bons.comptes.service.model.DecisionProjectionProject;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ContributeProject implements Command {
    @NotNull @NotBlank(message = "L'autheur ne peut être vide")
    private String author;
    @NotNull @Email @NotBlank(message = "L'e-mail ne peut être vide")
    private String mail;
    @NotNull @Min(0L)
    private int amount;

    public ContributeProject() {
    }

    public ContributeProject(JsonObject json) {
        this.author = json.getString("author");
        this.mail = json.getString("mail");
        this.amount = json.getInteger("amount");

    }

    public ContributeProject(String author, String mail, int amount) {
        this.author = author;
        this.mail = mail;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public ContributeProject setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public Event apply(DecisionProjectionProject decisionProjectionProject) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContributeProject that = (ContributeProject) o;
        return Objects.equals(author, that.author) &&
                Objects.equals(mail, that.mail) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, mail, amount);
    }
}
