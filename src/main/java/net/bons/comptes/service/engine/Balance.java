package net.bons.comptes.service.engine;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import com.google.common.collect.Lists;
import net.bons.comptes.service.model.Deal;
import net.bons.comptes.service.model.RawProject;

import java.util.*;
import java.util.stream.Collectors;

/**
 */
public class Balance {
    /**
     * The accounts.
     * key is name of account owner.
     * value is account (debt if negative).
     */
    private Map<String, Integer> accounts;

    public Map<String, Integer> getAccounts() {
        return accounts;
    }

    public Balance(RawProject project) {
        accounts = computeAccounts(project.getDeals());
    }
    public Balance(Collection<Deal> deals) {
        accounts = computeAccounts(deals);
    }

    public List<Deal> compute() {
        List<Map.Entry<String, Integer>> sortingAccounts = Lists.newArrayList(accounts.entrySet());
        Set<Map.Entry<String, Integer>> debtors = sortingAccounts.stream().filter((entry) -> entry.getValue() < 0).collect(


            Collectors.toSet());
        Set<Map.Entry<String, Integer>> creditors = sortingAccounts.stream().filter((entry) -> entry.getValue() > 0).collect(
            Collectors.toSet());

        final List<Deal> balance = new ArrayList<>();
        Iterator<Map.Entry<String, Integer>> creditorIter = creditors.iterator();
        Map.Entry<String, Integer> creditor = creditorIter.next();
        for (Map.Entry<String, Integer> debtor : debtors) {
            while (debtor.getValue() < 0) {
                final Deal deal = new Deal(creditor.getKey(), Math.min(debtor.getValue(), creditor.getValue()), "", debtor.getKey());
                balance.add(deal);
                debtor.setValue(debtor.getValue() - deal.getAmount()); // reduce the debt
                creditor.setValue(creditor.getValue() + deal.getAmount());
                if (creditor.getValue() >= 0 && creditorIter.hasNext()) {
                    creditor = creditorIter.next();
                }
            }
        }

        return balance;
    }

    /**
     * Compute account of each users.
     */
    private Map<String, Integer> computeAccounts(Collection<Deal> deals) {
        final Map<String, Integer> accounts = new HashMap<>();
        for (Deal deal : deals) {
            accounts.compute(deal.getCreditor(), (s, a) -> a == null ? deal.getAmount() : a + deal.getAmount());
            int debt = deal.getAmount() / deal.getDebtors().size();
            for (String debtor : deal.getDebtors()) {
                accounts.compute(debtor, (s, a) -> a == null ? - debt : a - debt);
            }
        }
        return accounts;
    }
}
