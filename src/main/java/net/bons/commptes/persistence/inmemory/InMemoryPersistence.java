package net.bons.commptes.persistence.inmemory;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import net.bons.commptes.persistence.CotizeDAO;
import net.bons.commptes.service.model.RawProject;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

@Singleton
public class InMemoryPersistence implements CotizeDAO {
    private Set<RawProject> datas = new HashSet<>(0);

    @Override
    public void storeProject(RawProject project) {
        datas.add(project);
    }

    @Override
    public void updateProject(RawProject oldProject, RawProject newProject) {

    }

    @Override
    public Optional<RawProject> loadProject(String identifiant) {
        return datas.stream()
                    .filter(proj -> proj.getIdentifier().equals(identifiant))
                    .findFirst();
    }

    @Override
    public String getIdentifiant() {
        String base = UUID.randomUUID().toString();
        Function<String, Predicate<RawProject>> exist = id -> prj -> prj.getIdentifier().equals(id);
        int size = 5;
        String id;
        do {
            id = base.substring(0, size++);
        } while (datas.stream().anyMatch(exist.apply(id)));

        return id;
    }

    @Override
    public String getPassAdmin() {
        return UUID.randomUUID().toString().substring(0, 5);
    }
}
