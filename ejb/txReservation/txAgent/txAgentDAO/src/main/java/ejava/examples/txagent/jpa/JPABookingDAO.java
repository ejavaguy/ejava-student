package ejava.examples.txagent.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.DAOException;
import ejava.examples.txagent.dao.BookingDAO;

public class JPABookingDAO implements BookingDAO {
    Log log = LogFactory.getLog(JPABookingDAO.class);
    
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Booking createBooking(Booking booking)
            throws DAOException {      
        try {
            log.debug("persisting booking:" + booking);
            
            em.persist(booking);            

            log.debug("booking persisted:" + booking);
            return booking;
        }
        catch (RuntimeException ex) {
            log.debug("error persisting booking:" + booking, ex);
            throw new DAOException("error persisting booking:" + 
                    booking, ex);
        }
    }

    public Booking getBooking(long id) throws DAOException {
        try {
            log.debug("getting booking:" + id);
            Booking booking = em.find(Booking.class, id);            
            log.debug("found booking:" + booking);
            return booking;
        }
        catch (RuntimeException ex) {
            log.debug("error getting booking:" + id, ex);
            throw new DAOException("error getting booking:" + id, ex);
        }
    }

    public List<Booking> getBookings(int index, int count) 
        throws DAOException {
        return getBookings("getAllBookings", null, index, count); 
    }

    @SuppressWarnings("unchecked")
    public List<Booking> getBookings(
            String queryName, Map<String, Object> params, 
            int index, int count) 
            throws DAOException {
        try {
            Query query = em.createNamedQuery(queryName)
                            .setFirstResult(index)
                            .setMaxResults(count);
            if (params != null && !params.keySet().isEmpty()) {
                for(String key: params.keySet()) {
                    query.setParameter(key, params.get(key));
                }                
            }
            List<Booking> bookings = query.getResultList();
            log.debug(bookings.size() + " bookings found");
            return bookings;            
        }
        catch (RuntimeException ex) {
            log.debug("error getting bookings:" + queryName + 
                    ", params=" + params, ex);
            throw new DAOException("error getting bookings:" + queryName + 
                    ", params=" + params, ex);
        }
    }

    public Booking removeBooking(Booking booking) 
        throws DAOException {
        try {
            log.debug("removing booking:" + booking);
            booking = em.find(Booking.class, 
                    booking.getId());
            em.remove(booking);
            log.debug("removed booking:" + booking);
            return booking;
        }
        catch (RuntimeException ex) {
            log.debug("error removing booking:" + booking, ex);
            throw new DAOException("error removing booking:" + 
                    booking, ex);
        }
    }

    public Booking updateBooking(Booking booking) throws DAOException {
        try {
            log.debug("merging booking:" + booking);
            Booking updated = em.merge(booking); 
            log.debug("merged booking:" + updated);
            return updated;
        }
        catch (RuntimeException ex) {
            log.debug("error merging booking:" + booking, ex);
            throw new DAOException("error merging booking:" + 
                    booking, ex);
        }
    }

    public Booking getBookingByConfirmation(String confirmation) 
        throws DAOException {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("confirmation", confirmation);
        return getBookings(
            "getBookingByConfirmation", params, 0, 1).get(0);        
    }

}
