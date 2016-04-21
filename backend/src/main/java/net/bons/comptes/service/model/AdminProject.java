package net.bons.comptes.service.model;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.stream.Collectors;

public class AdminProject implements Project {
    private String name;
    private String author;
    private String description;
    private String mail;
    private String identifier;
    private String passAdmin;
    private int amount;
    private ProjectOptions options;
    private Collection<Contribution> contributions;
    private Collection<Outgoing> outgoings;

    public AdminProject(JsonObject json) {
        this.name = json.getString("name");
        this.author = json.getString("author");
        this.description = json.getString("description");
        this.mail = json.getString("mail");
        this.identifier = json.getString("identifier");
        this.passAdmin = json.getString("passAdmin");
        this.amount = json.getInteger("amount", 0);
        this.contributions = Utils.extractArray(json, "contributions", Contribution::new);
        this.outgoings = Utils.extractArray(json, "outgoings", Outgoing::new);
        this.options = new ProjectOptions(json.getJsonObject("options"));
    }

    public AdminProject() {
    }

    public AdminProject(AdminProject project) {
        this.name = project.getName();
        this.author = project.getAuthor();
        this.description = project.getDescription();
        this.mail = project.getMail();
        this.identifier = project.getIdentifier();
        this.passAdmin = project.getPassAdmin();
        this.amount = project.getAmount();
        this.contributions = project.getContributions();
        this.outgoings = project.getOutgoings();
        this.options = new ProjectOptions(project.getOptions());
    }

    public AdminProject(RawProject project) {
        this.name = project.getName();
        this.author = project.getAuthor();
        this.description = project.getDescription();
        this.mail = project.getMail();
        this.identifier = project.getIdentifier();
        this.passAdmin = project.getPassAdmin();
        this.amount = project.getAmount();
        this.outgoings = project.getOutgoings();
        this.contributions = project.getContributions().stream()
                                    .map(contribution -> new Contribution(contribution.getContributionId(), contribution.getAuthor(), contribution.getAmount(), null, contribution.getPayed()))
                                    .collect(Collectors.toList());
        this.options = new ProjectOptions(project.getOptions());
    }

    public JsonObject toJson() {
        JsonArray jsonDeals = new JsonArray();
        contributions.stream().map(Contribution::toJson).forEach(jsonDeals::add);
        JsonArray jsonOutgoings = new JsonArray();
        outgoings.stream().map(Outgoing::toJson).forEach(jsonOutgoings::add);
        return new JsonObject()
                .put("name", this.name)
                .put("author", this.author)
                .put("description", this.description)
                .put("mail", this.mail)
                .put("identifier", this.identifier)
                .put("passAdmin", this.passAdmin)
                .put("amount", this.amount)
                .put("options", this.options.toJson())
                .put("contributions", jsonDeals)
                .put("outgoings", jsonOutgoings);
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

    public String getMail() {
        return mail;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassAdmin() {
        return passAdmin;
    }

    public Collection<Contribution> getContributions() {
        return contributions;
    }

    public ProjectOptions getOptions() {
        return options;
    }

    public Collection<Outgoing> getOutgoings() {
        return outgoings;
    }
}
