package net.bons.comptes.service.engine;

import net.bons.comptes.service.model.Deal;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BalanceTest {

  @Test
  public void testCompute() throws Exception {
    Collection<Deal> deals = new ArrayList<>();
    deals.add(new Deal("Albin", 15, "", "Benoît", "Michel", "Florent", "Albin"));
    deals.add(new Deal("Albin", 15, "", "Benoît", "Pierrick", "Florent", "Albin"));
    deals.add(new Deal("Benjamin", 10, "", "Dounia", "Pierrick", "Vanessa", "Benjamin"));
    deals.add(new Deal("Benjamin", 10, "", "Benoît", "Michel", "Vanessa", "Benjamin"));
    deals.add(new Deal("Florent", 20, "", "Benoît", "Michel", "Albin", "Benjamin", "Dounia", "Florent", "Pierrick"));
    deals.add(new Deal("Dounia", 15, "", "Benoît", "Michel", "Albin", "Benjamin", "Dounia", "Florent", "Pierrick"));
    Balance balance = new Balance(deals);
    List<Deal> compute = balance.compute();

    assert compute.isEmpty();
  }
}