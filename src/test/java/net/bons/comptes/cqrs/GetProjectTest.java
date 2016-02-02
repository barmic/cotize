package net.bons.comptes.cqrs;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class GetProjectTest {
    @Test
    public void test() {
        // given
        GetProject getProject = new GetProject(mock(MongoClient.class));
        JsonArray deals = new JsonArray();
        JsonObject deal = new JsonObject()
                .put("amount", 1000)
                .put("creditor", "Michel")
                .put("date", 1454425375674L)
                .put("email", "michel.barret@gmail.com");
        deals.add(deal);
        JsonObject project = new JsonObject()
                .put("amount", 1000)
                .put("author", "Michel Barret")
                .put("description", "C'est l'anniversaire de Vanessa")
                .put("email", "exemple@exemple.com")
                .put("identifier", "e06b1bf3-0")
                .put("name", "Anniversaire de Vanessa")
                .put("passAdmin", "01f8a0e0-b")
                .put("deals", deals);

        // when
        JsonObject filteredProject = getProject.filter(project);

        // then
        System.out.println("toto " + filteredProject.toString());
        Collection<String> forbidenFields = Arrays.asList("email", "passAdmin");
        boolean present = filteredProject.fieldNames().stream()
                .filter(forbidenFields::contains).findFirst().isPresent();
        assertThat(present).isFalse();

        JsonArray deals1 = filteredProject.getJsonArray("deals");
        Collection<String> legalFields = Arrays.asList("creditor");
        deals1.forEach(d -> {
            JsonObject jsonDeal = (JsonObject) d;
            boolean pres = jsonDeal.fieldNames().stream().filter(f -> !legalFields.contains(f)).findFirst().isPresent();
            assertThat(pres).isFalse();
        });
    }
}
