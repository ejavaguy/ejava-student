package org.myorg.jpatickets.bl;

import java.util.ArrayList;

import java.util.List;

import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.dao.VenueDAO;

public class VenueMgmtImpl implements VenueMgmt {
    VenueDAO dao;
    
    public void setDao(VenueDAO dao) {
        this.dao = dao;
    }

    @Override
    public Venue createVenue(Venue venue, int sections, int positions, int rows) {
        List<Seat> seats = new ArrayList<Seat>(sections * positions * rows);
        for (int s=0; s<sections; s++) {
            String section = Character.toString((char)('A'+s));
            for (int row=0; row<rows; row++) {
                for (int pos=0; pos<positions; pos++) {
                    Seat seat = new Seat(venue, section, row, pos);
                    seats.add(seat);
                }
            }
        }
        dao.saveVenue(venue, seats);
        return venue;
    }

    @Override
    public Venue getVenue(String venueId) {
        return dao.getVenue(venueId);
    }
}
