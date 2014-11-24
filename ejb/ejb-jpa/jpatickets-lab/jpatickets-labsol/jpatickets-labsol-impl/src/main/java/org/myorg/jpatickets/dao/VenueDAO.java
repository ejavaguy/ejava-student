package org.myorg.jpatickets.dao;

import java.util.List;

import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Venue;

public interface VenueDAO {
    void saveVenue(Venue venue, List<Seat> seats);
    Venue getVenue(String venueId);
    List<Seat> getSeatsForVenue(Venue venue, int offset, int limit);
}
