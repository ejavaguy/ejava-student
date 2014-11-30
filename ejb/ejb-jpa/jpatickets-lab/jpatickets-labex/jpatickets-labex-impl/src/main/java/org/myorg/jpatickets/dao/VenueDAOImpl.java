package org.myorg.jpatickets.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Venue;

public class VenueDAOImpl implements VenueDAO {
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public void saveVenue(Venue venue, List<Seat> seats) {
        em.setFlushMode(FlushModeType.COMMIT);
        em.persist(venue);        
        if (seats!=null) {
            for (Seat s: seats) {
                em.persist(s);
            }
        }
        em.setFlushMode(FlushModeType.AUTO);
    }
    
    public Venue getVenue(String venueId) {
        return em.find(Venue.class, venueId);
    }
    
    @Override
    public List<Seat> getSeatsForVenue(Venue venue, int offset, int limit) {
        TypedQuery<Seat> query = em.createNamedQuery("JPATicketSeat.getSeatsForVenue", 
                Seat.class)
                .setParameter("venue", venue);
        if (offset > 0) { query.setFirstResult(offset); }
        if (limit > 0) { query.setMaxResults(limit); }
        return query.getResultList();
    }
}
