package ejava.examples.txagent.jpa;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.AgentReservationSession;
import ejava.examples.txagent.bl.BookingAgent;
import ejava.examples.txagent.blimpl.AgentImpl;
import ejava.examples.txagent.blimpl.AgentSessionImpl;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;
import ejava.examples.txhotel.ejb.HotelReservationSessionRemote;
import ejava.examples.txhotel.ejb.TestUtilRemote;
import ejava.util.ejb.EJBClient;
import ejava.util.jndi.JNDIUtil;

public abstract class AgentBLTestBase {
    protected static final Log log = LogFactory.getLog(AgentBLTestBase.class);
    private static final String PERSISTENCE_UNIT = "txagent-test";
    protected BookingDAO bookingDAO;
    protected static EntityManagerFactory emf; 
    protected EntityManager em;
    
    protected static String reservationistName = 
        System.getProperty("jndi.name.hotel", 
        	EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB", 
        		"HotelRegistrationEJB", HotelRegistrationRemote.class.getName()));    
    protected static String reservationistSessionName = 
        System.getProperty("jndi.name.hotelsession", 
			EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB", 
	        	"HotelReservationSessionEJB", HotelReservationSessionRemote.class.getName()));
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    	log.info("*** setUpClass() ***");
    	log.debug("creating entityManagerFactory:" + PERSISTENCE_UNIT);
        emf=Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

		//give application time to fully deploy
		if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
			long waitTime=15000;
	    	log.info(String.format("pausing %d secs for server deployment to complete", waitTime/1000));
	    	Thread.sleep(waitTime);
		}
		else {
	    	log.info(String.format("startstop not set"));
		}
    }
    
    @Before
    public void setUp() throws Exception {
    	log.info("*** setUp() ***");
    	
    	try {
	    	log.debug("creating entityManager");
	        em = emf.createEntityManager();
	        bookingDAO = new JPABookingDAO();
	        ((JPABookingDAO)bookingDAO).setEntityManager(em);
	        log.debug("em=" + em);
    	} catch (Throwable ex) {
    		ex.printStackTrace();
    		fail("unexpected error in setUp()" + ex);
    	}
                
        cleanup();
        em.getTransaction().begin();
    }
    
    public BookingAgent getBookingAgent() throws NamingException {
    	InitialContext jndi = new InitialContext();
    	
    	BookingAgent agent = new AgentImpl();
        HotelReservationist reservationist = JNDIUtil.lookup(
	        	jndi, HotelRegistrationRemote.class, reservationistName, 15);
	        log.debug("reservationist=" + reservationist);

        ((AgentImpl)agent).setBookingDAO(bookingDAO);
        ((AgentImpl)agent).setReservationist(reservationist);
    	return agent;
    }
    
    public AgentReservationSession getReservationSession() throws NamingException {
    	InitialContext jndi = new InitialContext();
        AgentReservationSession agentSession = new AgentSessionImpl();
        HotelReservationSession reservationSession = JNDIUtil.lookup(
    		jndi, HotelReservationSessionRemote.class, reservationistSessionName, 15);
    	
        ((AgentSessionImpl)agentSession).setBookingDAO(bookingDAO);
        ((AgentSessionImpl)agentSession).setReservationist(reservationSession);
    	return agentSession;
    }
    

    @After
    public void tearDown() throws Exception {
    	log.info("*** tearDown() ***");
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
        em=null;
    }
    
    @AfterClass
    public static void tearDownClass() {
    	log.info("*** tearDownClass() ***");
    	if (emf != null) {
    		emf.close();
    		emf=null;
    	}
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() throws NamingException, AgentReservationException {
        log.info("cleaning up database");
        em.getTransaction().begin();
        List<Booking> bookings = 
            em.createQuery("select b from Booking b").getResultList();
        for(Booking b: bookings) {
            em.remove(b);
        }
        em.getTransaction().commit();
        

        BookingAgent agent=getBookingAgent();
        assertEquals("unexpected bookings", 0, agent.getBookings(0, 100).size());
        
        //use remote interface to cleanup the hotel side of the dual application 
        String hotelHelperName = EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB", 
        		"TestUtilEJB", TestUtilRemote.class.getName());
        
        TestUtilRemote testUtil=JNDIUtil.lookup(new InitialContext(), 
    				TestUtilRemote.class, hotelHelperName, 5);
        testUtil.reset();
    }
    
    protected void populate() {
        log.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
