package net.bons.comptes.cqrs.event;

/* Licence Public Barmic
 * copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
 */

public interface Event {
    String getProjectId();

    long getCreation();
}
