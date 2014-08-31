package ejava.examples.txhotel.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
//import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txhotel.bl.HotelReservationException;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.blimpl.HotelReservationImpl;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.dao.ReservationDAO;
import ejava.examples.txhotel.jpa.JPAReservationDAO;

/**
 * This class provides a stateless EJB wrapper around stateless logic 
 * that performs operations on the hotel.
 *
 * @author jcstaff
 */
//@Stateless - will be supplied in ejb-jar.xml due to multi deploy
public class HotelRegistrationEJB implements HotelRegistrationRemote,
        HotelRegistrationLocal {
    private Log log = LogFactory.getLog(HotelRegistrationEJB.class);
    
    @PersistenceContext(unitName="txhotel")
    private EntityManager em;
    
    @Resource
    private SessionContext ctx;

    //initialized by init() method
    private HotelReservationist reservationist;
    

    /** 
     * This method is called when the bean is created and is responsble for
     * instantiating the business logic implementation, a DAO, and assigning
     * the EntityManager for the DAO(s) to use.
     */
    @PostConstruct
    public void init() {
        log.info("*** HotelRegistrationEJB initializing ***");
        log.debug("em=" + em);
        log.debug("ctx=" + ctx);
        
        ReservationDAO dao = new JPAReservationDAO();
        ((JPAReservationDAO)dao).setEntityManager(em);
        reservationist = new HotelReservationImpl();
        ((HotelReservationImpl)reservationist).setDao(dao);
    }
    
    /**
     * This method is called whenever the EJB is evicted from the container;
     * which is not too often for stateless session beans under normal 
     * circumstances.
     */
    @PreDestroy
    public void close() {
        reservationist=null;
    }    

    /**
     * This method will cause the current transaction to be rolled back
     * if the business logic reports an exception cancelling a reservation.
     */
    public void cancelReservation(Reservation reservation)
            throws HotelReservationException {
        try {
            reservationist.cancelReservation(reservation);
        }
        catch (HotelReservationException ex) {
            log.info("error canceling reservation, rolling back transaction:"
                    + ex.getMessage());
            ctx.setRollbackOnly();
            throw ex;
        }
    }

    /**
     * This method is just a pass-thru to the business logic.
     */
    public Reservation commitReservation(Reservation reservation)
            throws HotelReservationException {
        return reservationist.commitReservation(reservation);
    }

    /**
     * This method will rollack the current transaction if the business
     * logic throws an exception while trying to create a reservation.
     */
    public Reservation createReservation(Person person, Date startDate,
            Date endDate) throws HotelReservationException {
        try {
           return reservationist.createReservation(person, startDate, endDate);
        }
        catch (HotelReservationException ex) {
            log.info("error creating reservation, rolling back transaction:"
                    + ex.getMessage());
            ctx.setRollbackOnly();
            throw ex;
        }
    }

    public List<Reservation> getReservations(int index, int count)
            throws HotelReservationException {
        return reservationist.getReservations(index, count);
    }

    public List<Reservation> getReservationsForPerson(Person person, int index,
            int count) throws HotelReservationException {
        return reservationist.getReservationsForPerson(person, index, count);
    }

    public Reservation getReservationByConfirmation(String confirmation) 
        throws HotelReservationException {
        return reservationist.getReservationByConfirmation(confirmation);
    }

    public void cleanupReservation(String confirmation) 
        throws HotelReservationException {
        reservationist.cleanupReservation(confirmation);
    }
}
