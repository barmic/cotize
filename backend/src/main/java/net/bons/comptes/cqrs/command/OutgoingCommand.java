package net.bons.comptes.cqrs.command;

import io.vertx.core.json.JsonObject;
import net.bons.comptes.service.model.Outgoing;

/**
 *
 */
public class OutgoingCommand implements Command {
    private String author;
    private int amount;
    private String description;

    public OutgoingCommand(String author, int amount, String description) {
        this.author = author;
        this.amount = amount;
        this.description = description;
    }

    public OutgoingCommand() {
    }

    public OutgoingCommand(Outgoing outgoing) {
        this.amount = outgoing.getAmount();
        this.author = outgoing.getAuthor();
        this.description = outgoing.getDescription();
    }

    public OutgoingCommand(JsonObject jsonObject) {
        amount = jsonObject.getInteger("amount");
        author = jsonObject.getString("author");
        description = jsonObject.getString("description");
    }

    public String getAuthor() {
        return author;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
