package net.bons.comptes.service.model;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@DataObject
public class Deal {
    private String creditor;
    private Collection<String> debtors;
    private int amount;
    private String email;

    private Long date;
    private String name;

    public Deal() {
    }

    public Deal(Deal deal) {
        this.creditor = deal.getCreditor();
        this.amount = deal.getAmount();
        this.email = deal.getEmail();
        this.debtors = deal.getDebtors();
        this.date = deal.getDate();
    }

    public Deal(String creditor, int amount, String email, String...debtors) {
        this.creditor = creditor;
        this.amount = amount;
        this.email = email;
        this.debtors = Arrays.asList(debtors);
        this.date = System.currentTimeMillis();
    }

    public Deal(JsonObject json) {
        this.creditor = json.getString("creditor");
        this.amount = json.getInteger("amount");
        this.email = json.getString("email");
        this.debtors = json.getJsonArray("debtors").stream().map(Objects::toString).collect(Collectors.toSet());
        this.date = json.getLong("date");
    }

    public JsonObject toJson() {
        JsonArray jsonDebtors = new JsonArray();
        debtors.forEach(jsonDebtors::add);
        return new JsonObject()
                .put("creditor", creditor)
                .put("amount", amount)
                .put("email", email)
                .put("debtors", jsonDebtors)
                .put("date", date);
    }

    public String getCreditor() {
        return creditor;
    }

    public Collection<String> getDebtors() {
        return debtors;
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
}
