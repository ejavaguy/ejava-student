package ejava.examples.txagent.ejbclient;

import java.util.Calendar;

import static org.junit.Assert.*;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.AgentReservationSession;
import ejava.examples.txagent.bl.BookingAgent;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.ejb.AgentReservationSessionRemote;
import ejava.examples.txagent.ejb.BookingAgentRemote;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;
import ejava.examples.txhotel.ejb.TestUtilRemote;
import ejava.util.ejb.EJBClient;
import ejava.util.jndi.JNDIUtil;

public class AgentReservationSessionIT {
    private static final Log log = LogFactory.getLog(AgentReservationSessionIT.class);
    InitialContext jndi;
    String agentJNDI = System.getProperty("jndi.name.agent",
    	EJBClient.getRemoteLookupName("txAgentEAR", "txAgentEJB",  
    		"BookingAgentEJB", BookingAgentRemote.class.getName()));
    String agentsessionJNDI = System.getProperty("jndi.name.agentsession",
    	EJBClient.getRemoteLookupName("txAgentEAR", "txAgentEJB",  
        	"AgentReservationSessionEJB", AgentReservationSessionRemote.class.getName()));
    AgentReservationSession agentSession;
    BookingAgent agent;    
    String hotelJNDI = System.getProperty("jndi.name.hotel",
		EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB",
    		"HotelRegistrationEJB", HotelRegistrationRemote.class.getName()));
    HotelReservationist hotel;
    

	@BeforeClass
	public static void setUpClass() throws Exception {
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
    
    @Before()
    public void setUp() throws Exception {
    	boolean fail=false;
    	
        log.debug("getting jndi initial context");
        try {
            jndi = new InitialContext();
            log.debug("jndi=" + jndi.getEnvironment());
        } catch (Exception ex) {
                fail("Error getting InitialContext:" + ex);
        }    

        try {
            log.info("looking up:" + agentJNDI);
            //address server start-up delays on first lookup
        	agent=JNDIUtil.lookup(jndi, BookingAgentRemote.class, agentJNDI, 15);
            log.info("found:" + agent);
        } catch (Exception ex) {
                log.error("Error looking up:" + agentJNDI, ex);
                fail=true; //but lets keep going
        }    

        try {
            log.info("looking up:" + agentsessionJNDI);
            agentSession = (AgentReservationSessionRemote)
                jndi.lookup(agentsessionJNDI);
            log.info("found:" + agentSession);
        } catch (Exception ex) {
                log.error("Error looking up:" + agentsessionJNDI, ex);
                fail=true; //but lets keep going
        }    

        try {
            log.info("looking up:" + hotelJNDI);
            hotel = (HotelRegistrationRemote)jndi.lookup(hotelJNDI);
            log.info("found:" + hotel);
        } catch (Exception ex) {
                log.error("Error looking up:" + hotelJNDI, ex);
                fail=true; //but lets keep going
        }    
        
        //okay - now we can either fail or get on with the test setup
        if (fail) { fail("jndi errors in our setup -- look at previous output for specific errors"); }
        else {
            try {
                    cleanup();
            } catch (Exception ex) {
                    fail(ex.toString());
            }
        }
    }
    
    private void cleanup() throws Exception {
        List<Booking> bookings = agent.getBookings(0, 100);
        while (bookings.size() > 0) {
            for(Booking b: bookings) {
                log.debug("cleaning up booking# " + b.getConfirmation());
                agent.cleanupBooking(b.getConfirmation());
            }
            bookings = agent.getBookings(0, 100);
        }     
        
        //use remote interface to cleanup the hotel side of the dual application 
        String hotelHelperName = EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB", 
        		"TestUtilEJB", TestUtilRemote.class.getName());
        
        TestUtilRemote testUtil=
    		JNDIUtil.lookup(new InitialContext(), TestUtilRemote.class, hotelHelperName, 5);
        testUtil.reset();
    }

    @Test
    public void testCreate() throws Exception {
        log.info("*** testCreate: ***");       

        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            hotel.getReservationsForPerson(person, 0, 100);
        log.debug("starting with " + baseline.size() +
                " reservations for " + person);

        agentSession.createBooking();


        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        for(int i=0; i<10; i++) {
            start.add(Calendar.DAY_OF_YEAR, (i*7));
            end.add(Calendar.DAY_OF_YEAR, (i*7)+2);
            agentSession.addReservation(
                    person, start.getTime(), end.getTime());
        }
                
        List<Reservation> mine = 
            hotel.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        log.debug("successfully verified that uncommited agent session has " +
                "not posted reservations to hotel");
        
        Booking booking = agentSession.commit();
        assertEquals("unexpected number of confirmations in booking",
                10,booking.getHotelConfirmations().size());
        assertEquals("unexpected number of reservations in booking",
                10,booking.getHotelReservations().size());
                
        mine = hotel.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size()+10,mine.size());
        log.debug("successfully verified that commited agent session has " +
            "posted reservations to hotel");
    }
    
    @Test
    public void testBadCreate() throws Exception {
        log.info("*** testBadCreate: ***");       
        
        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            hotel.getReservationsForPerson(person, 0, 100);
        log.debug("starting with " + baseline.size() +
                " reservations for " + person);

        agentSession.createBooking();


        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<9; i++) {
            agentSession.addReservation(
                    person, start.getTime(), end.getTime());
        }
        //now create a bad one
        agentSession.addReservation(
                person, end.getTime(),start.getTime());
        
        List<Reservation> mine = 
            hotel.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        log.debug("successfully verified that uncommited agent session has " +
                "not posted reservations to hotel");
        
        try {
            agentSession.commit();
            fail("bad reservation wasn't detected");
        }
        catch (AgentReservationException ex) {
            log.debug("got expected exception:" + ex);
        }
        
        //if everyone was a part of the tx, none should have been added
        mine = hotel.getReservationsForPerson(person, 0, 100);
        if (mine.size() == baseline.size()) {
            log.info("all reservations were rolled back");
        }
        else {
            log.info("" + (mine.size() - baseline.size()) + 
                    " reservations were not rolled back");            
        }        
    }
}
