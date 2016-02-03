package net.bons.comptes.service.model;

import com.google.common.collect.Lists;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Project {
    private String name;
    private String author;
    private String description;
    private String email;
    private String identifier;
    private String passAdmin;
    private int amount;
    private Collection<Deal> deals;

    public Project(JsonObject json) {
        this.name = json.getString("name");
        this.author = json.getString("author");
        this.description = json.getString("description");
        this.email = json.getString("email");
        this.identifier = json.getString("identifier");
        this.passAdmin = json.getString("passAdmin");
        this.amount = json.getInteger("amount", 0);
        if (json.containsKey("deals")) {
            this.deals = json.getJsonArray("deals").stream().map(o -> new Deal((JsonObject) o)).collect(Collectors.toList());
        }
        else {
            this.deals = Collections.emptyList();
        }
    }

    public Project() {
    }

    public Project(Project project) {
        this.name = project.getName();
        this.author = project.getAuthor();
        this.description = project.getDescription();
        this.email = project.getEmail();
        this.identifier = project.getIdentifier();
        this.passAdmin = project.getPassAdmin();
        this.amount = project.getAmount();
        this.deals = Collections.emptyList();
    }

    Project(String name, String author, String description, String email, String identifier,
               String passAdmin, Collection<Deal> deals) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.email = email;
        this.identifier = identifier;
        this.passAdmin = passAdmin;
        this.deals = deals;
        this.amount = deals.stream().collect(Collectors.summingInt(Deal::getAmount));
    }

    public JsonObject toJson() {
        JsonArray jsonDeals = new JsonArray();
        deals.stream().map(d -> d.toJson()).forEach(jsonDeals::add);
        return new JsonObject()
                .put("name", this.name)
                .put("author", this.author)
                .put("description", this.description)
                .put("email", this.email)
                .put("identifier", this.identifier)
                .put("passAdmin", this.passAdmin)
                .put("amount", this.amount)
                .put("deals", jsonDeals);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Project project) {
        return new Builder(project);
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

    public int getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassAdmin() {
        return passAdmin;
    }

    public Collection<Deal> getDeals() {
        return deals;
    }

    public static class Builder {
        private String name;
        private String author;
        private String description;
        private String email;
        private String identifier;
        private String passAdmin;
        private Collection<Deal> deals;

        public Builder() {
            deals = Collections.emptyList();
        }

        public Builder(Project project) {
            name = project.getName();
            author = project.getAuthor();
            description = project.getDescription();
            email = project.getEmail();
            identifier = project.getIdentifier();
            passAdmin = project.getPassAdmin();
            deals = Lists.newArrayList(project.getDeals());
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder passAdmin(String passAdmin) {
            this.passAdmin = passAdmin;
            return this;
        }

        public Builder deals(Collection<Deal> deals) {
            this.deals = deals;
            return this;
        }

        public Project createRawProject() {
            return new Project(name, author, description, email, identifier, passAdmin, deals);
        }
    }
}
