package net.bons.commptes.persistence.mongo.mapper;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.bons.commptes.persistence.mongo.Fields;
import net.bons.commptes.service.model.RawProject;

import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectToDBObject implements Function<RawProject, DBObject> {
    @Override
    public DBObject apply(RawProject project) {
        BasicDBObject dbObject = new BasicDBObject();
//        dbObject.append(Fields.identifier.name(), project.getIdentifier());
//        dbObject.append(Fields.name.name(), project.getName());
//        dbObject.append(Fields.description.name(), project.getDescription());
//        dbObject.append(Fields.author.name(), project.getAuthor());
//        dbObject.append(Fields.email.name(), project.getEmail());
//        dbObject.append(Fields.pass_admin.name(), project.getPassAdmin());
//
//        dbObject.append(Fields.deal.name(), project.getDeals().stream().map(deal -> {
//            BasicDBObject dbDeal = new BasicDBObject();
//            dbDeal.append(Fields.Deal.name.name(), deal.getName());
//            dbDeal.append(Fields.Deal.amount.name(), deal.getAmount());
//            dbDeal.append(Fields.Deal.date.name(), deal.getDate());
//            dbDeal.append(Fields.Deal.creditor.name(), deal.getCreditor());
//            dbDeal.append(Fields.Deal.debtor.name(), deal.getDebtors());
//            return dbDeal;
//        }).collect(Collectors.toList()));

        return dbObject;
    }

    @Override
    public <V> Function<V, DBObject> compose(Function<? super V, ? extends RawProject> before) {
        return null;
    }

    @Override
    public <V> Function<RawProject, V> andThen(Function<? super DBObject, ? extends V> after) {
        return null;
    }
}
