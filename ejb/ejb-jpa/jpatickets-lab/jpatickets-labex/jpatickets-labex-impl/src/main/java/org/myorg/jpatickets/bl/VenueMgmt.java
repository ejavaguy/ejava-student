package org.myorg.jpatickets.bl;

import org.myorg.jpatickets.bo.Venue;

public interface VenueMgmt {
    Venue createVenue(Venue venue, int sections, int positions, int rows);
    Venue getVenue(String venueId);
}
