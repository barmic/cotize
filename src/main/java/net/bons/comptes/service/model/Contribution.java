package net.bons.comptes.service.model;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.json.JsonObject;

public class Contribution {
    private String contributionId;
    private String author;
    private int amount;
    private String email;

    private Long date;
    private String name;

    public Contribution() {
    }

    public Contribution(Contribution contribution) {
        this.contributionId = contribution.getContributionId();
        this.author = contribution.getAuthor();
        this.amount = contribution.getAmount();
        this.email = contribution.getEmail();
        this.date = contribution.getDate();
    }

    public Contribution(String contributionId, String author, int amount, String email, String...debtors) {
        this.contributionId = contributionId;
        this.author = author;
        this.amount = amount;
        this.email = email;
        this.date = System.currentTimeMillis();
    }

    public Contribution(JsonObject json) {
        this.contributionId = json.getString("contributionId");
        this.author = json.getString("author");
        this.amount = json.getInteger("amount");
        this.email = json.getString("email");
        this.date = json.getLong("date");
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("contributionId", contributionId)
                .put("author", author)
                .put("amount", amount)
                .put("email", email)
                .put("date", date);
    }

    public String getContributionId() {
        return contributionId;
    }

    public Contribution setContributionId(String contributionId) {
        this.contributionId = contributionId;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public int getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public Contribution setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Contribution setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public Contribution setEmail(String email) {
        this.email = email;
        return this;
    }

    public Contribution setDate(Long date) {
        this.date = date;
        return this;
    }

    public Contribution setName(String name) {
        this.name = name;
        return this;
    }
}
