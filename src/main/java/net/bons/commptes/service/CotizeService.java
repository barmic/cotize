package net.bons.commptes.service;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import net.bons.commptes.ProjectInput;
import net.bons.commptes.persistence.CotizeDAO;
import net.bons.commptes.service.exception.BadRequest;
import net.bons.commptes.service.model.AnonimizedProject;
import net.bons.commptes.service.model.Deal;
import net.bons.commptes.service.model.Project;
import net.bons.commptes.service.model.RawProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class CotizeService {
    private CotizeDAO persistence;

    public CotizeService(CotizeDAO persistence) {
        this.persistence = persistence;
    }

    public RawProject createProject(ProjectInput projectInput) {
        RawProject project = RawProject.builder()
                                       .author(projectInput.getAuthor())
                                       .name(projectInput.getName())
                                       .description(projectInput.getDescription())
                                       .email(projectInput.getEmail())
                                       .identifier(persistence.getIdentifiant())
                                       .passAdmin(persistence.getPassAdmin())
                                       .createRawProject();
        persistence.storeProject(project);
        return project;
    }

    /**
     * Retrieve a project from data base.
     * If the key correspond to pass admin, the result is complet, else the resultis anonymized.
     *
     * @param identifiant project id
     * @param key         pass admin of project
     * @return project anonymized or not.
     * @throws BadRequest
     */
    public Project getProject(String identifiant, String key) throws BadRequest {
        Optional<RawProject> result = persistence.loadProject(identifiant);
        if (!result.isPresent()) {
            throw new BadRequest("Le projet " + identifiant + " n'existe pas");
        }
        return annonimizeProject(result.get(), key);
    }

    /**
     * Return raw project if the key correspond to pass admin,
     * else an annonimized project is returned.
     *
     * @param rawProject
     * @param key
     * @return
     */
    private Project annonimizeProject(RawProject rawProject, String key) {
        return Objects.equals(key, rawProject.getPassAdmin()) ? rawProject
                                                              : new AnonimizedProject(rawProject.getAmount(),
                                                                                      rawProject.getName(),
                                                                                      rawProject.getAuthor(),
                                                                                      rawProject.getDescription());
    }

    /**
     *
     * @param identifier
     * @param deal
     * @throws BadRequest
     */
    public void contribute(String identifier, Deal deal) throws BadRequest {
        Optional<RawProject> project = persistence.loadProject(identifier);
        if (!project.isPresent()) {
            throw new BadRequest("Le projet " + identifier + " n'existe pas");
        }
        RawProject currentProject = project.get();
//        Optional<Fields.Deal> deal1 = currentProject.getDeals().stream()
//                                                    .filter(d -> d.getCreditor().equalsIgnoreCase(deal.getCreditor()))
//                                                    .findFirst();

        if (true) {
            // TODO MAJ
            throw new BadRequest("La contribution de " + deal.getCreditor() + " existe déjà");
        } else {
            Collection<Deal> newDeals = new ArrayList<>(currentProject.getDeals());
            newDeals.add(deal);
            RawProject newProject = RawProject.builder(currentProject)
                                              .deals(newDeals)
                                              .createRawProject();
            persistence.updateProject(currentProject, newProject);
        }
    }
}
