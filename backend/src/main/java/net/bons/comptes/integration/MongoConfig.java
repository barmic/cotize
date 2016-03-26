package net.bons.comptes.integration;

/**
 *
 */
public class MongoConfig {
    private String projectCollection;

    public MongoConfig(String projectCollection) {
        this.projectCollection = projectCollection;
    }

    public String getProjectCollection() {
        return projectCollection;
    }
}
