package net.bons.commptes.persistence.mongo.mapper;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.bons.commptes.persistence.mongo.Fields;
import net.bons.commptes.service.model.Deal;
import net.bons.commptes.service.model.RawProject;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DBObjectToProject implements Function<BasicDBObject, RawProject> {
    @Override
    public RawProject apply(BasicDBObject basicDBObject) {
        RawProject.Builder project
            = RawProject.builder()
                              .name(extract(basicDBObject, Fields.name))
                              .author(extract(basicDBObject, Fields.author))
                              .description(extract(basicDBObject, Fields.description))
                              .email(extract(basicDBObject, Fields.email))
                              .identifier(extract(basicDBObject, Fields.identifier))
                              .passAdmin(extract(basicDBObject, Fields.pass_admin));

        BasicDBList deals = (BasicDBList) basicDBObject.get(Fields.deal.name());
        Collection<Deal> bizDeals = deals.stream().map(obj -> {
            BasicDBObject dbObj = ((BasicDBObject) obj);
            // TODO il manque les debtors
            return new Deal(
                extract(dbObj, Fields.Deal.creditor),
                extract(dbObj, Fields.Deal.amount),
                extract(dbObj, Fields.Deal.email)
          );
        }).collect(Collectors.toList());
        project.deals(bizDeals);
        return project.createRawProject();
    }


  private <T> T extract(BasicDBObject dbObj, Fields.Deal field) {
    return (T) dbObj.getObjectId(field.name());
  }

  private <T> T extract(BasicDBObject dbObj, Fields field) {
    return (T) dbObj.getObjectId(field.name());
  }

  @Override
    public <V> Function<V, RawProject> compose(Function<? super V, ? extends BasicDBObject> before) {
        return null;
    }

    @Override
    public <V> Function<BasicDBObject, V> andThen(Function<? super RawProject, ? extends V> after) {
        return null;
    }
}
