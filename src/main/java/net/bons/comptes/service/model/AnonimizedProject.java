package net.bons.comptes.service.model;

public class AnonimizedProject implements Project {
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
}
