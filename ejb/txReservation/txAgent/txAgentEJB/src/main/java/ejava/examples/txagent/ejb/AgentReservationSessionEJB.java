package ejava.examples.txagent.ejb;

import java.rmi.RemoteException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.AgentReservationSession;
import ejava.examples.txagent.blimpl.AgentSessionImpl;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txagent.jpa.JPABookingDAO;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.ejb.HotelReservationSessionRemote;

/**
 * This class provides an example of a Stateful session bean that will try to
 * coordinate its local transaction with the tranaction of a remote session
 * bean. The actual configuration may not really proove anything since the
 * two applications will probably be deployed to the same server. We would
 * need to break them apart into separate servers and separate server 
 * implementations to show anything really significant.<p/>
 * 
 * Note too that this session bean declares its transaction properties using
 * class annotations. By default, this stateful session bean runs without
 * a tranaction while caching reservations for the user. A transaction is 
 * required only for the commit() call, which also signals the removal of
 * this bean instance.<p/>
 * 
 * The bean also implements the javax.ejb.SessionSynchronization interface.
 * This allows it to be called at the start and end of a transaction so that
 * it has a chance to update its cached values appropriately. This bean,
 * however, just prints some debug.
 *
 * @author jcstaff
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AgentReservationSessionEJB
    implements AgentReservationSessionLocal, AgentReservationSessionRemote,
    SessionSynchronization {
    private static final Log log = 
        LogFactory.getLog(AgentReservationSessionEJB.class);
    
    @Resource
    private SessionContext ctx;
    
    @PersistenceContext(unitName="txagent")
    private EntityManager em;
    
    @EJB(name="ejb/HotelReservationSession")
    private HotelReservationSessionRemote reservationSession;

    private AgentReservationSession agentSession;
    
    /**
     * This method is called immediately after dependency injection and 
     * before any business method because of the @PostConstruct annotation. 
     * It provides a chance to do any manual construction of the overall bean. 
     * In this case, we need to instantiate out business logic implementation, 
     * provide it a DAO, and register the EntityManager for the DAO(s) to use.
     */
    @PostConstruct
    public void init() {
        log.info("*** AgentReservationSessionEJB ***");
        
        //these objects were injected using only annotations within class
        log.debug("ctx=" + ctx);
        log.debug("em=" + em);
        //this object was injected using a ejb-ref in ejb-jar.xml
        log.debug("reservationSession=" + reservationSession);
        
        BookingDAO dao = new JPABookingDAO();
        ((JPABookingDAO)dao).setEntityManager(em);
        agentSession = new AgentSessionImpl();
        ((AgentSessionImpl)agentSession).setBookingDAO(dao);
        ((AgentSessionImpl)agentSession).setReservationist(reservationSession);
    }
    
    /**
     * This method should be called when the bean is destroyed because of 
     * the @PreDestory annotation.
     */
    @PreDestroy
    public void closing() {
        log.info("*** AgentReservationSessionEJB closing ***");
    }

    public void createBooking() throws AgentReservationException {
        agentSession.createBooking();        
    }       

    public void addReservation(Person person, Date startDate, Date endDate) 
        throws AgentReservationException {
        agentSession.addReservation(person, startDate, endDate);        
    }

    public void cancelBooking() throws AgentReservationException {
        agentSession.cancelBooking();        
    }
    
    /**
     * This method implements the big-bang. All information, up to this
     * point is being cached in the business logic instance and remote
     * hotel reservation session; all owned by this specific instance.
     * This method causes a transaction to be started, the remote reservations
     * to be commited by the hotel, and the booking to be stored locally
     * with references to the reservations. If it all works; all should be
     * stored. If something fails (e.g., an invalid reservation date), then
     * nothing will be saved.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Booking commit() throws AgentReservationException {
        return agentSession.commit();
    }
    
    @Remove(retainIfException=false)
    public void close() {
	    log.info("*** AgentReservationSessionEJB close ***");
	    agentSession.close();
	}
    
    /**
     * This method is called at the beginning of a transaction because of
     * the implements SessionSynchronization interface.
     */
    public void afterBegin()  {
        log.debug("*** Transaction Started ***");        
    }
    /**
     * This method is called just before the transaction has committed to 
     * give the bean a chance to complain.
     */
    public void beforeCompletion() throws EJBException, RemoteException {
        log.debug("*** Transaction about to complete, rollback:" + 
                ctx.getRollbackOnly() + " ***");
    }       
    /**
     * This method is called just after the transaction has committed to 
     * tell us what happened.
     */
    public void afterCompletion(boolean status)  {
        log.debug("*** Transaction Completed:" + status + " ***");        
    }
}
