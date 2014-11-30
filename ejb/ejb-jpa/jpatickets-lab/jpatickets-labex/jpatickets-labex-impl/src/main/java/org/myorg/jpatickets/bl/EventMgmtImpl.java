package org.myorg.jpatickets.bl;

import java.math.BigDecimal;
import java.util.List;

import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Ticket;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.dao.EventMgmtDAO;
import org.myorg.jpatickets.dao.VenueDAO;

public class EventMgmtImpl implements EventMgmt {
    private EventMgmtDAO edao;
    private VenueDAO vdao;
    
    public void setEventDAO(EventMgmtDAO edao) {
        this.edao = edao;
    }
    public void setVenueDAO(VenueDAO vdao) {
        this.vdao = vdao;
    }

    @Override
    public Event createEvent(Event event, Venue venue) throws UnavailableException {
        BigDecimal maxPrice = new BigDecimal(1000);
        List<Seat> seats = vdao.getSeatsForVenue(venue, 0, 0);
        for (Seat seat: seats) {
            Ticket ticket = new Ticket(event, seat);
            BigDecimal discount = new BigDecimal(seat.getRow()*.1);
            ticket.setPrice(maxPrice.subtract(maxPrice.multiply(discount)));
            event.withTicket(ticket);
        }
        return edao.createEvent(event);
    }

    @Override
    public Event getEvent(int id) {
        return edao.getEvent(id);
    }
    @Override
    public Event fetchEventTickets(int id) {
        return edao.fetchEventTickets(id);
    }
    @Override
    public Event fetchEventTicketsSeats(int id) {
        return edao.fetchEventTicketsSeats(id);
    }

    @Override
    public List<Seat> findSeats(Event event, String section, Integer row, Integer position, int offset, int limit) {
        return edao.findSeats(event, section, row, position, offset, limit);
    }
    
    @Override
    public List<Ticket> getTickets(Event event, List<Seat> seats) {
        return edao.findTickets(event, seats);
    }

    @Override
    public List<Ticket> reserveSeats(Event event, List<Seat> seats) throws UnavailableException {
        if (event==null) { throw new IllegalArgumentException("no event provided"); }
        if (seats==null) { throw new IllegalArgumentException("no seat selected"); }

        List<Ticket> tickets = getTickets(event, seats);
        for (Ticket ticket: tickets) {
            if (ticket.isSold()) {
                throw new UnavailableException(String.format("seat %s already sold", ticket.getSeat()));
            } else {
                ticket.setSold(true);
            }
        }
        return tickets;
    }
}
