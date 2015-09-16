package net.bons.commptes.service.model;

/*
 * Licence Public Barmic
 * copyright 2014 Michel Barret <michel.barret@gmail.com>
 */

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

//@Data
//@EqualsAndHashCode(exclude={"date"})
public class Deal {
    private String creditor;
    private List<String> debtors;
    private int amount;
    private String email;

    private Date date;
    private String name;

    public Deal(String creditor, int amount, String email, String...debtors) {
        this.creditor = creditor;
        this.amount = amount;
        this.email = email;
        this.debtors = Arrays.asList(debtors);
        this.date = new Date();
    }

    public String getCreditor() {
        return creditor;
    }

    public List<String> getDebtors() {
        return debtors;
    }

    public int getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
