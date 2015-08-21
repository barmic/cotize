package net.bons.commptes.persistence;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import net.bons.commptes.service.model.RawProject;

import java.util.Optional;

public interface CotizeDAO {
    void storeProject(RawProject project);
    void updateProject(RawProject oldProject, RawProject newProject);
    Optional<RawProject> loadProject(String hash);
    String getIdentifiant();
    String getPassAdmin();
}
