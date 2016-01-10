package net.bons.comptes.cqrs.query;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.collection.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetProject implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetProject.class);
  private static final Set<String> publicFields = HashSet.of("amount", "author", "date", "name",
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
      routingContext.put("body", project);
    }

    LOG.info("projectId : {}; adminPass : {}", adminPass);
    routingContext.next();
  }

  private JsonObject filter(JsonObject project) {
    // TODO filter the contribution
    Map<String, Object> collect = project.stream()
                                         .filter(entry -> publicFields.contains(entry.getKey()))
                                         .map(entry -> Tuple.of(entry.getKey(), entry.getValue()))
                                         .collect(HashMap.collector());
    collect.get("contributions")
           .toStream()
           .map(o -> (JsonArray) o)
           .flatMap(JsonArray::getList)
           .forEach(obj -> ((JsonObject) obj).remove("mail"));
    return new JsonObject(collect.toJavaMap(Function.identity()));
  }
}
