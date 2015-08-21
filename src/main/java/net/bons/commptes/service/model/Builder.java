package net.bons.commptes.service.model;

import com.google.common.collect.ImmutableList;

public class Builder {
    private String name;
    private String author;
    private String description;
    private String email;
    private String identifier;
    private String passAdmin;
    private ImmutableList<Deal> deals;

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

    public Builder deals(ImmutableList<Deal> deals) {
        this.deals = deals;
        return this;
    }

    public RawProject createRawProject() {
        return new RawProject(name, author, description, email, identifier, passAdmin, deals);
    }
}