package net.bons.comptes.service.model;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

//@ToString
//@Getter
//@EqualsAndHashCode
public class RawProject implements Project {
    private String name;
    private String author;
    private String description;
    private String email;
    private String identifier;
    private String passAdmin;
    private int amount;
    private Collection<Deal> deals;

    RawProject(String name, String author, String description, String email, String identifier,
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

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RawProject rawProject) {
        return new Builder(rawProject);
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

        public Builder(RawProject rawProject) {
            name = rawProject.getName();
            author = rawProject.getAuthor();
            description = rawProject.getDescription();
            email = rawProject.getEmail();
            identifier = rawProject.getIdentifier();
            passAdmin = rawProject.getPassAdmin();
            deals = Lists.newArrayList(rawProject.getDeals());
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

        public RawProject createRawProject() {
            return new RawProject(name, author, description, email, identifier, passAdmin, deals);
        }
    }
}
