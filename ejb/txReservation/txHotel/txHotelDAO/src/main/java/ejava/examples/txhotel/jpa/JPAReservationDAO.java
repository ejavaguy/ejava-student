package ejava.examples.txhotel.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.dao.DAOException;
import ejava.examples.txhotel.dao.ReservationDAO;

public class JPAReservationDAO implements ReservationDAO {
    private Log log = LogFactory.getLog(JPAReservationDAO.class);
    
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Reservation createReservation(Reservation reservation)
            throws DAOException {      
        try {
            log.debug("persisting reservation:" + reservation);
            
            //look for the person in the database
            Person person = reservation.getPerson();
            if (person != null && person.getId() != 0) {
                person = em.find(Person.class, person.getId());
                reservation.setPerson(person);
                log.debug("found existing person:" + person);
            }
            
            em.persist(reservation);            
            log.debug("reservation persisted:" + reservation);
            return reservation;
        }
        catch (RuntimeException ex) {
            log.debug("error persisting reservation:" + reservation, ex);
            throw new DAOException("error persisting reservation:" + 
                    reservation, ex);
        }
    }

    public Reservation getReservation(long id) throws DAOException {
        try {
            log.debug("getting reservation:" + id);
            Reservation reservation = em.find(Reservation.class, id);            
            log.debug("found reservation:" + reservation);
            return reservation;
        }
        catch (RuntimeException ex) {
            log.debug("error getting reservation:" + id, ex);
            throw new DAOException("error getting reservation:" + id, ex);
        }
    }

    public List<Reservation> getReservations(int index, int count) 
        throws DAOException {
        return getReservations("getAllReservations", null, index, count); 
    }

    @SuppressWarnings("unchecked")
    public List<Reservation> getReservations(
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
            List<Reservation> reservations = query.getResultList();
            log.debug(reservations.size() + " reservations found");
            return reservations;            
        }
        catch (RuntimeException ex) {
            log.debug("error getting reservations:" + queryName + 
                    ", params=" + params, ex);
            throw new DAOException("error getting reservations:" + queryName + 
                    ", params=" + params, ex);
        }
    }

    public Reservation removeReservation(Reservation reservation) 
        throws DAOException {
        try {
            log.debug("removing reservation:" + reservation);
            reservation = em.find(Reservation.class, 
                    reservation.getId());
            em.remove(reservation);
            log.debug("removed reservation:" + reservation);
            return reservation;
        }
        catch (RuntimeException ex) {
            log.debug("error removing reservation:" + reservation, ex);
            throw new DAOException("error removing reservation:" + 
                    reservation, ex);
        }
    }

    public Reservation updateReservation(Reservation reservation) throws DAOException {
        try {
            log.debug("merging reservation:" + reservation);
            Reservation updated = em.merge(reservation); 
            log.debug("merged reservation:" + updated);
            return updated;
        }
        catch (RuntimeException ex) {
            log.debug("error merging reservation:" + reservation, ex);
            throw new DAOException("error merging reservation:" + 
                    reservation, ex);
        }
    }

    public Reservation getReservationByConfirmation(String confirmation) 
        throws DAOException {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("confirmation", confirmation);
        return getReservations(
            "getReservationsByConfirmation", params, 0, 1).get(0);        
    }
}
