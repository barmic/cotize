package net.bons.comptes.cqrs.query;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GetProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);
  private static final Collection<String> publicFields = ImmutableSet.of("amount", "author", "date", "name",
      "projectId", "description");

  private MongoClient mongoClient;
  private LoadProject loadProject;

  @Inject
  public GetProject(MongoClient mongoClient, LoadProject loadProject) {
    this.loadProject = loadProject;
    this.mongoClient = mongoClient;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    LOG.info("Get Project");
    final String projectId = routingContext.request().getParam("projectId");
    final String adminPass = routingContext.request().getParam("adminPass");

    JsonObject query = new JsonObject().put("projectId", projectId);

    mongoClient.find("CotizeEvents", query, res -> {
          if (res.succeeded()) {
            JsonObject project = loadProject.load(res.result());
            if (adminPass == null) {
              project = filter(project);
            } else if (!Objects.equals(adminPass, project.getString("admin"))) {
              // TODO error
            }
            routingContext.put("body", project.toString());
          } else {
            LOG.error(res.cause().getLocalizedMessage());
            routingContext.put("body", "");
          }
          routingContext.next();
        });

    LOG.info("projectId : {}; adminPass : {}", projectId, adminPass);
  }

  private JsonObject filter(JsonObject project) {
    Map<String, Object> collect = project.stream()
                                         .filter(entry -> publicFields.contains(entry.getKey()))
                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return new JsonObject(collect);
  }
}
