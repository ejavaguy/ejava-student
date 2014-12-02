package org.myorg.jpatickets.ejb;

import javax.ejb.Remote;

import org.myorg.jpatickets.bo.Venue;

@Remote
public interface VenueMgmtRemote {
    Venue createVenue(Venue venue, int sections, int positions, int rows);
    Venue getVenue(String venueId);
}
