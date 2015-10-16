package net.bons.comptes.cqrs.query;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
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
      "projectId", "description", "contributions");

  @Inject
  public GetProject() {
  }

  @Override
  public void handle(RoutingContext routingContext) {
    LOG.info("Get Project");
    final String adminPass = routingContext.request().getParam("adminPass");

    JsonObject project = routingContext.get("project");
    if (project == null) {
      routingContext.fail(404);
    } else {
      if (adminPass == null) {
        project = filter(project);
      } else if (!Objects.equals(adminPass, project.getString("admin"))) {
        // TODO error
      }
      routingContext.put("body", project.toString());
    }

    LOG.info("projectId : {}; adminPass : {}", adminPass);
    routingContext.next();
  }

  private JsonObject filter(JsonObject project) {
    // TODO filter the contribution
    Map<String, Object> collect = project.stream()
                                         .filter(entry -> publicFields.contains(entry.getKey()))
                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return new JsonObject(collect);
  }
}
