package ejava.examples.txagent.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.BookingAgent;
import ejava.examples.txagent.blimpl.AgentImpl;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txagent.jpa.JPABookingDAO;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;

/**
 * This class provides a few conveniences methods to help inspect the
 * impact of the actions taken with the stateful AgentReservationSession bean.
 * It is stateless and can answer questions and perform cleanup actions 
 * associated with Bookings. 
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BookingAgentEJB implements BookingAgentRemote,
        BookingAgentLocal {
    private static final Logger log = LoggerFactory.getLogger(BookingAgentEJB.class);
    
    @EJB(name="ejb/HotelReservation")
    //@EJB(mappedName="ejb:txHotelEAR/txHotelEJB//HotelRegistrationEJB!ejava.examples.txhotel.ejb.HotelRegistrationRemote")
    private HotelRegistrationRemote hotel;
    
    @PersistenceContext(unitName="txagent")
    private EntityManager em;
    
    @Resource
    private SessionContext ctx;

    //initialized within init() method
    private BookingAgent agent;


    /**
     * This method creates the business logic, assigns a DAO, and registers
     * the EntityManager for the DAO(s) to use.
     */
    @PostConstruct
    public void init() {
        log.info("*** BookingAgentEJB initializing ***");
        
        //injected by container
        log.debug("em=" + em);
        log.debug("ctx=" + ctx);
        log.debug("hotel=" + hotel);
        
        //the following ugly debug was for testing with classloader issues
        if (hotel == null) {
	        StringBuilder text = new StringBuilder();
	        try {
	            Object object = ctx.lookup("ejb/HotelReservation");
	            text.append("ctx.lookup(ejb/HotelReservation)=" + object);
	            text.append(", class=" + object.getClass().getName());
	            @SuppressWarnings("rawtypes")
				Class clazz = object.getClass();
	            for (Object iface : clazz.getInterfaces()) {
	            	text.append(", iface=" + iface);
	            }
	            hotel = (HotelRegistrationRemote) object;
	        }
	        catch (Exception ex) { 
	        	text.append(", exception=" + ex);
	        	try {
		            Object object = new javax.naming.InitialContext().lookup(
		            		"ejava/examples/txhotel/HotelRegistrationEJB/remote");
		            text.append(", jndi.lookup(HotelRegistrationEJB)=" + object);
		            text.append(", class=" + object.getClass().getName());
		            @SuppressWarnings("rawtypes")
					Class clazz = object.getClass();
		            for (Object iface : clazz.getInterfaces()) {
		            	text.append(", iface=" + iface);
		            }
		            hotel = (HotelRegistrationRemote) object;
	        	}
		        catch (Exception ex2) { 
		         	text.append(", exception=" + ex2);
		        }
	        }
	        finally              { log.debug("{}",text); }
        }
        
        //manual construction
        BookingDAO dao = new JPABookingDAO();
        ((JPABookingDAO)dao).setEntityManager(em);
        agent = new AgentImpl();
        ((AgentImpl)agent).setReservationist(hotel);
        ((AgentImpl)agent).setBookingDAO(dao);
    }
    
    /**
     * This method is called whenever the bean is ejected from the container;
     * which isn't too often for stateless session beans under normal 
     * circumstances.
     */
    @PreDestroy
    public void close() {
        log.info("*** BookingAgentEJB closing ***");
        agent=null;
        hotel=null;
    }    

    public Booking getBookingByConfirmation(String confirmation) 
        throws AgentReservationException {
        return agent.getBookingByConfirmation(confirmation);
    }

    public List<Booking> getBookings(int index, int count) 
        throws AgentReservationException {
        return agent.getBookings(index, count);
    }

    public void cleanupBooking(String confirmation) 
        throws AgentReservationException {
        agent.cleanupBooking(confirmation);
    }
}
