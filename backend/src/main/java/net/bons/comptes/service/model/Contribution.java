package net.bons.comptes.service.model;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.json.JsonObject;

public class Contribution implements JsonModel {
    private String contributionId;
    private String author;
    private int amount;
    private String mail;

    private Long date;
    private String name;
    private Boolean payed;

    public Contribution() {
    }

    public Contribution(Contribution contribution) {
        this.contributionId = contribution.getContributionId();
        this.author = contribution.getAuthor();
        this.amount = contribution.getAmount();
        this.mail = contribution.getMail();
        this.date = contribution.getDate();
        this.payed = contribution.getPayed();
    }

    public Contribution(String contributionId, String author, int amount, String mail, Boolean payed, String...debtors) {
        this.contributionId = contributionId;
        this.author = author;
        this.amount = amount;
        this.mail = mail;
        this.date = System.currentTimeMillis();
        this.payed = payed;
    }

    public Contribution(JsonObject json) {
        this.contributionId = json.getString("contributionId");
        this.author = json.getString("author");
        this.amount = json.getInteger("amount");
        this.mail = json.getString("mail");
        this.date = json.getLong("date");
        this.payed = json.getBoolean("payed", false);
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("contributionId", contributionId)
                .put("author", author)
                .put("amount", amount)
                .put("mail", mail)
                .put("date", date)
                .put("payed", payed);
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

    public String getMail() {
        return mail;
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

    public Contribution setMail(String mail) {
        this.mail = mail;
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

    public Boolean getPayed() {
        return payed;
    }

    public Contribution setPayed(Boolean payed) {
        this.payed = payed;
        return this;
    }
}
