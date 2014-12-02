package org.myorg.jpatickets.bl;

import java.util.List;

import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Ticket;
import org.myorg.jpatickets.bo.Venue;

public interface EventMgmt {
    Event createEvent(Event event, Venue venue) throws UnavailableException;
    Event getEvent(int id);
    List<Seat> findSeats(Event event, String section, Integer row, Integer position, int offset, int limit);
    List<Ticket> getTickets(Event event, List<Seat> seats);
    List<Ticket> reserveSeats(Event event, List<Seat> seats) throws UnavailableException;
    Event fetchEventTickets(int id);
    Event fetchEventTicketsSeats(int id);
}
