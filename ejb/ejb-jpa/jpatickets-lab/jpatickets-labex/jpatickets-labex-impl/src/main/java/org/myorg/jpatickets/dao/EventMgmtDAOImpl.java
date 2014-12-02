package org.myorg.jpatickets.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Ticket;

public class EventMgmtDAOImpl implements EventMgmtDAO {
    EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Event createEvent(Event event) {
        em.persist(event);
        return event;
    }
    
    @Override
    public Event getEvent(int eventId) {
        return em.find(Event.class, eventId);
    }
    
    @Override
    public Event fetchEventTickets(int id) {
        List<Event> events = em.createNamedQuery("JPATicketEvent.fetchEventTickets", 
                Event.class)
                .setParameter("eventId", id)
                .getResultList();
        return events.isEmpty() ? null : events.get(0);
    }    
    
    @Override
    public Event fetchEventTicketsSeats(int id) {
        List<Event> events = em.createNamedQuery("JPATicketEvent.fetchEventTicketsSeats", 
                Event.class)
                .setParameter("eventId", id)
                .getResultList();
        return events.isEmpty() ? null : events.get(0);
    }
    
    @Override
    public Map<String, Object> fetchEventDTOData(int eventId) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNamedQuery("JPATicketEvent.fetchEventDTO")
                .setParameter("eventId", eventId)
                .getResultList();
        
        Map<String, Object> dtoData = new HashMap<String, Object>();
        if (!rows.isEmpty()) {
            Object[] row = rows.get(0);
            Event event = (Event) row[0];
            String venueName = (String) row[1];
            Number numTickets = (Number) row[2];
            dtoData.put(EVENT, event);
            dtoData.put(VENUE_NAME, venueName);
            dtoData.put(NUM_TICKETS, numTickets.intValue());
        }
        return dtoData;
    }
    
    @Override
    public List<Seat> findSeats(Event event, String section, Integer row,
            Integer position, int offset, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Seat> qdef = cb.createQuery(Seat.class);
        Root<Ticket> t = qdef.from(Ticket.class);
        Join<Ticket, Event> e = t.join("event");
        Join<Ticket, Seat> s = t.join("seat");
        
        Predicate pred = cb.conjunction();
        List<Expression<Boolean>> expr = pred.getExpressions();
        if (section != null) {
            expr.add(cb.equal(s.get("pk").get("section"), section));
        }
        if (row !=null) {
            expr.add(cb.equal(s.get("pk").get("row"), row));
        }
        if (position !=null) {
            expr.add(cb.equal(s.get("pk").get("position"), position));
        }
        
        qdef.select(s).where(cb.and(cb.equal(e, event), pred)).orderBy(cb.asc(t.get("price")));
        TypedQuery<Seat> query = em.createQuery(qdef);
        if (offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        
        return query.getResultList();
    }
    
    @Override
    public List<Ticket> findTickets(Event event, List<Seat> seats) {
        return em.createNamedQuery("JPATicketTicket.findTickets", 
                Ticket.class)
                .setParameter("seats", seats)
                .getResultList();
    }
}
