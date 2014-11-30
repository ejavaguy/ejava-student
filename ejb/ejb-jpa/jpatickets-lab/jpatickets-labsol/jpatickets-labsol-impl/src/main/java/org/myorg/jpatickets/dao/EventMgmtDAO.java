package org.myorg.jpatickets.dao;

import java.util.List;
import java.util.Map;

import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Ticket;

public interface EventMgmtDAO {
    String EVENT = "event";
    String VENUE_NAME = "venueName";
    String NUM_TICKETS = "numTickets";
    
    Event createEvent(Event event);
    Event getEvent(int id);
    List<Seat> findSeats(Event event, String section, Integer row,
            Integer position, int offset, int limit);
    List<Ticket> findTickets(Event event, List<Seat> seats);
    
    Event fetchEventTickets(int id);
    Event fetchEventTicketsSeats(int id);
    Map<String, Object> fetchEventDTOData(int eventId);
}
