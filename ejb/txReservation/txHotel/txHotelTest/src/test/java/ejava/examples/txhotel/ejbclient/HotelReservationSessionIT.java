package ejava.examples.txhotel.ejbclient;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.ejb.HotelReservationSessionRemote;
import ejava.util.ejb.EJBClient;
import ejava.util.jndi.JNDIUtil;

/**
 * This test case provides an example of conducting a failed usecase 
 * across a set of EJBs that have a mixture of transaction scope.
 * @author jcstaff
 *
 */
public class HotelReservationSessionIT extends HotelRemoteTestBase {
    private static final Log log = LogFactory.getLog(HotelReservationSessionIT.class);
    static InitialContext jndi;
    static final String sessionJNDI = System.getProperty("jndi.name.hotelsession",
    	EJBClient.getEJBClientLookupName("txHotelEAR", "txHotelEJB", "", 
        	"HotelReservationSessionEJB", HotelReservationSessionRemote.class.getName(), true));
    static final String requiredJNDI = System.getProperty("jndi.name.hotelsession",
    	EJBClient.getEJBClientLookupName("txHotelEAR", "txHotelEJB", "", 
        	"RequiredSessionEJB", HotelReservationSessionRemote.class.getName(), true));
    static final String requiresNewJNDI = System.getProperty("jndi.name.hotelsession",
    	EJBClient.getEJBClientLookupName("txHotelEAR", "txHotelEJB", "", 
        	"RequiresNewSessionEJB", HotelReservationSessionRemote.class.getName(), true));
    
    
    private HotelReservationist reservationist;
    
    public static HotelReservationist getReservationist() throws NamingException {
    	return HotelReservationIT.getReservationist();
    }
    
    public static Map<String, HotelReservationist> getReservationists() throws NamingException {
       	return HotelReservationIT.getReservationists();
    }
    
    static Map<String, HotelReservationSession> getReservationSessions() throws NamingException {
    	log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi name:" + sessionJNDI);        
        Map<String, HotelReservationSession> reservationSessions =
        		new HashMap<String, HotelReservationSession>();
        //have the first lookup be willing to wait for application to fully deploy
        //lets just store one of each type and not the default
        //HotelReservationSession registrar =  	
    		JNDIUtil.lookup(jndi, HotelReservationSession.class, sessionJNDI, 15);
       	//reservationSessions.put("registrar", registrar);
       	reservationSessions.put("required", (HotelReservationSession)jndi.lookup(requiredJNDI));
       	reservationSessions.put("requiresNew", (HotelReservationSession)jndi.lookup(requiresNewJNDI));
       	return reservationSessions;
    }
    
    @Before
    public void setUp() throws Exception {
    	log.info("** setUp ***");
    	reservationist=getReservationist();
        cleanup();
    }

	private void cleanup() throws Exception {
	    List<Reservation> reservations = reservationist.getReservations(0, 100);
        while (reservations.size() > 0) {
            for(Reservation r: reservations) {
                try { reservationist.cancelReservation(r); }
                catch (Exception ex) {}
            }
            reservations = reservationist.getReservations(0, 100);
        }        
    }

	@Test
    public void testCreates() throws Exception {
		Map<String, HotelReservationSession> reservationSessions = getReservationSessions();
        for(String key: reservationSessions.keySet()) {
            testCreates(key, reservationSessions.get(key));
        }
        reservationSessions.clear();
    }

    private void testCreates(String key, HotelReservationSession session) 
        throws Exception {
        log.info("*** testCreates:" + key + " ***");       
        
        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            reservationist.getReservationsForPerson(person, 0, 100);

        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<10; i++) {
            session.createReservation(
                    person, start.getTime(), end.getTime());
        }
        
        List<Reservation> mine = 
            reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        
        List<Reservation> reservations = session.commit();
        assertEquals(10,reservations.size());
        
        mine = reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size()+10,mine.size());
        //session.close();
    }
    
    @Test
    public void testBadCreates() throws Exception {
		Map<String, HotelReservationSession> reservationSessions = getReservationSessions();
        for(String key: reservationSessions.keySet()) {
            testBadCreates(key, reservationSessions.get(key));
        }
        reservationSessions.clear();
    }
    private void testBadCreates(String key, HotelReservationSession session) 
        throws Exception {
        log.info("*** testBadCreates:" + key + " ***");       
        
        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            reservationist.getReservationsForPerson(person, 0, 100);
        
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<9; i++) {
            session.createReservation(
                    person, start.getTime(), end.getTime());
        }
        //now create a bad one
        session.createReservation(
                person, end.getTime(),start.getTime());
        
        List<Reservation> mine = 
            reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        
        try {
            session.commit();
            fail("bad reservation wasn't detected");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
            //session.close();
        }
        
        //if everyone was a part of the tx, none should have been added
        mine = reservationist.getReservationsForPerson(person, 0, 100);
        if (mine.size() == baseline.size()) {
            log.info("all reservations were rolled back for: " + key);
        }
        else {
            log.info("" + (mine.size() - baseline.size()) + 
                    " reservations were not rolled back for: " + key);            
        }        
    }
}
