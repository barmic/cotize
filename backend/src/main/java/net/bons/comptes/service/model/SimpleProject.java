package net.bons.comptes.service.model;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SimpleProject implements Project {
    private String name;
    private String author;
    private String description;
    private String mail;
    private String identifier;
    private int amount;
    private Seq<Contribution> contributions;

    public SimpleProject(JsonObject json) {
        this.name = json.getString("name");
        this.author = json.getString("author");
        this.description = json.getString("description");
        this.mail = json.getString("mail");
        this.identifier = json.getString("identifier");
        this.amount = json.getInteger("amount", 0);
        this.contributions = Utils.extractArray(json, "contributions", Contribution::new);
    }

    public SimpleProject() {
    }

    public SimpleProject(SimpleProject project) {
        this.name = project.getName();
        this.author = project.getAuthor();
        this.description = project.getDescription();
        this.mail = project.getMail();
        this.identifier = project.getIdentifier();
        this.amount = project.getAmount();
        this.contributions = project.getContributions();
    }

    public SimpleProject(RawProject project) {
        this.name = project.getName();
        this.author = project.getAuthor();
        this.description = project.getDescription();
        this.mail = project.getMail();
        this.identifier = project.getIdentifier();
        this.amount = project.getAmount();
        this.contributions = project.getContributions()
                                    .map(contribution -> new Contribution(null, contribution.getAuthor(), 0, null, null));
    }

    SimpleProject(String name, String author, String description, String mail, String identifier, Seq<Contribution> contributions) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.mail = mail;
        this.identifier = identifier;
        this.contributions = contributions;
        this.amount = contributions.map(Contribution::getAmount).sum().intValue();
    }

    @Override
    public JsonObject toJson() {
        JsonArray jsonDeals = new JsonArray();
        contributions.map(Contribution::toJson).forEach(jsonDeals::add);
        return new JsonObject()
                .put("name", this.name)
                .put("author", this.author)
                .put("description", this.description)
                .put("mail", this.mail)
                .put("identifier", this.identifier)
                .put("amount", this.amount)
                .put("contributions", jsonDeals);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SimpleProject project) {
        return new Builder(project);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    public String getMail() {
        return mail;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public Seq<Contribution> getContributions() {
        return contributions;
    }

    public static class Builder {
        private String name;
        private String author;
        private String description;
        private String mail;
        private String identifier;
        private Seq<Contribution> contributions;

        public Builder() {
            contributions = Stream.empty();
        }

        public Builder(SimpleProject project) {
            name = project.getName();
            author = project.getAuthor();
            description = project.getDescription();
            mail = project.getMail();
            identifier = project.getIdentifier();
            contributions = project.getContributions();
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

        public Builder mail(String mail) {
            this.mail = mail;
            return this;
        }

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder deals(Seq<Contribution> contributions) {
            this.contributions = contributions;
            return this;
        }

        public Project createRawProject() {
            return new SimpleProject(name, author, description, mail, identifier, contributions);
        }
    }
}
