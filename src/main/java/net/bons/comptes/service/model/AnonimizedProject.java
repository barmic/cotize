package net.bons.comptes.service.model;

public class AnonimizedProject {
    private int amount;
    private String name;
    private String author;
    private String description;

    public AnonimizedProject(int amount, String name, String author, String description) {
        this.amount = amount;
        this.name = name;
        this.author = author;
        this.description = description;
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
}
