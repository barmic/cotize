package net.bons.comptes.integration;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
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
