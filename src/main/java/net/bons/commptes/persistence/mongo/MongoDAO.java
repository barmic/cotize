package net.bons.commptes.persistence.mongo;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import net.bons.commptes.persistence.CotizeDAO;
import net.bons.commptes.persistence.mongo.mapper.DBObjectToProject;
import net.bons.commptes.persistence.mongo.mapper.ProjectToDBObject;
import net.bons.commptes.service.model.RawProject;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class MongoDAO implements CotizeDAO {
    private static final String DBNAME = "bonscomptes";
    public static final String COLLECTION_NAME = "projects";

    private MongoClient mongoClient;
    private Function<RawProject, DBObject> projectToDbObject;
    private Function<BasicDBObject, RawProject> dbObjectToProject;

    public MongoDAO() throws UnknownHostException {
        mongoClient = new MongoClient();
        projectToDbObject = new ProjectToDBObject();
        dbObjectToProject = new DBObjectToProject();
    }

    @Override
    public void storeProject(RawProject project) {
        DBCollection collection = getDbCollection();

        collection.insert(projectToDbObject.apply(project));
    }

    @Override
    public void updateProject(RawProject oldProject, RawProject newProject) {
        DBCollection collection = getDbCollection();

        collection.update(projectToDbObject.apply(oldProject), projectToDbObject.apply(newProject));
    }

    @Override
    public Optional<RawProject> loadProject(String hash) {
        Optional<RawProject> result = Optional.empty();
        DBCollection collection = getDbCollection();

        BasicDBObject criteria = new BasicDBObject();
        criteria.append(Fields.identifier.toString(), hash);
        Iterator<DBObject> iterator = collection.find(criteria).iterator();
        if (iterator.hasNext()) {
            result = Optional.of(dbObjectToProject.apply((BasicDBObject) iterator.next()));
        }
        return result;
    }

    private DBCollection getDbCollection() {
//        MongoDatabase db = mongoClient.getDatabase(DBNAME);
//        return db.getCollection(COLLECTION_NAME, );
        return null;
    }

    @Override
    public String getIdentifiant() {
        String candidate;
        do {
            candidate = UUID.randomUUID().toString();
        } while (loadProject(candidate).isPresent());
        return candidate;
    }

    @Override
    public String getPassAdmin() {
        return UUID.randomUUID().toString().substring(0, 5);
    }
}
