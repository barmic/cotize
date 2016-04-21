package net.bons.comptes.service.model;

import io.vertx.core.json.JsonObject;

/**
 *
 */
public class Outgoing implements JsonModel {
    private String author;
    private int amount;
    private String description;

    public Outgoing(String author, int amount, String description) {
        this.author = author;
        this.amount = amount;
        this.description = description;
    }

    public Outgoing() {
    }

    public Outgoing(Outgoing outgoing) {
        this.amount = outgoing.getAmount();
        this.author = outgoing.getAuthor();
        this.description = outgoing.getDescription();
    }

    public Outgoing(JsonObject jsonObject) {
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

    @Override
    public JsonObject toJson() {
        return new JsonObject().put("amount", amount)
                               .put("author", author)
                               .put("description", description);
    }
}
