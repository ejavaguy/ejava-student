package org.myorg.jpatickets.dao;

import java.util.List;

import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Ticket;

public interface EventMgmtDAO {
    Event createEvent(Event event);
    Event getEvent(int id);
    List<Seat> findSeats(Event event, String section, Integer row,
            Integer position, int offset, int limit);
    List<Ticket> findTickets(Event event, List<Seat> seats);
}
