package ejava.examples.txhotel.ejbclient;

import static org.junit.Assert.*;

import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;
import ejava.examples.txhotel.ejb.TestUtilRemote;
import ejava.util.ejb.EJBClient;
import ejava.util.jndi.JNDIUtil;

public class HotelReservationIT extends HotelRemoteTestBase {
    static final Logger log = LoggerFactory.getLogger(HotelReservationIT.class);
    static String registrarJNDI = System.getProperty("jndi.name.hotel",
    	EJBClient.getEJBClientLookupName("txHotelEAR", "txHotelEJB", "", 
    		"HotelRegistrationEJB", HotelRegistrationRemote.class.getName(), false));
    static final String requiredJNDI = System.getProperty("jndi.name.hotel.required",
    	EJBClient.getEJBClientLookupName("txHotelEAR", "txHotelEJB", "", 
    		"RequiredEJB", HotelRegistrationRemote.class.getName(), false));
    static final String requiresNewJNDI = System.getProperty("jndi.name.hotel.requiresNew",
    	EJBClient.getEJBClientLookupName("txHotelEAR", "txHotelEJB", "", 
    		"RequiresNewEJB", HotelRegistrationRemote.class.getName(), false));

    public static HotelReservationist getReservationist() throws NamingException {
    	return JNDIUtil.lookup(new InitialContext(), 
    		HotelReservationist.class, registrarJNDI, 15);
    }

    public static Map<String,HotelReservationist> getReservationists() 
    		throws NamingException {
        Context jndi = new InitialContext();
        try {
	        Map<String,HotelReservationist> reservationists = 
	                new HashMap<String, HotelReservationist>();
	        //have the first lookup be willing to wait for the application to fully load
	        HotelReservationist registrar = JNDIUtil.lookup(jndi, 
	        		HotelReservationist.class, registrarJNDI, 15); 
	        reservationists.put("registrar", registrar);
	        reservationists.put("required", (HotelReservationist)jndi.lookup(requiredJNDI));
	        reservationists.put("requiresNew", (HotelReservationist)jndi.lookup(requiresNewJNDI));
	        return reservationists;
        }
        finally {
            if (jndi != null) { jndi.close(); }
        }
    }
    
    @Before() 
    public void setUp() throws NamingException {
    	log.info("*** setUp() ***");
        String hotelHelperName = EJBClient.getEJBClientLookupName("txHotelEAR", "txHotelEJB", "", 
        		"TestUtilEJB", TestUtilRemote.class.getName(), false);
        //have first lookup be willing to wait until application fully deployed
        TestUtilRemote testUtil = JNDIUtil.lookup(new InitialContext(), TestUtilRemote.class, hotelHelperName, 15);
        testUtil.reset();
    }

    @Test
    public void testCreateReservation() throws Exception {
    	Map<String, HotelReservationist> reservationists = getReservationists();
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestCreateReservation(key, reservationist);
        }
    }        
    public void doTestCreateReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testCreateReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        
        Reservation reservation = reservationist.createReservation(
                person, start.getTime(), end.getTime());
        log.debug("created reservation:" + reservation);
        assertNotNull("unexpected confirmation id", 
                reservation.getConfirmation());
    }
    
    @Test
    public void testGetReservation() throws Exception {
    	Map<String, HotelReservationist> reservationists = getReservationists();
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestGetReservation(key, reservationist);
        }
    }        
    public void doTestGetReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testGetReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        
        Reservation reservation = reservationist.createReservation(
                person, start.getTime(), end.getTime());
        log.debug("created reservation:" + reservation);
        assertNotNull("unexpected confirmation id", 
                reservation.getConfirmation());
        
        Reservation reservation2 = 
            reservationist.getReservationByConfirmation(
                    reservation.getConfirmation());
        assertNotNull("reservation wasn't found", reservation2);
    }

    @Test
    public void testBadCreateReservation() throws Exception {
    	Map<String, HotelReservationist> reservationists = getReservationists();
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestBadCreateReservation(key, reservationist);
        }
    }        
    public void doTestBadCreateReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testBadCreateReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, -2);
        
        @SuppressWarnings("unused")
        Reservation reservation = null;
        try {
            reservation = reservationist.createReservation(
                person, null, end.getTime());
            fail("didn't catch null start date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }

        try {
            reservation = reservationist.createReservation(
                person, start.getTime() , null);
            fail("didn't catch null end date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }

        try {
            reservation = reservationist.createReservation(
                person, start.getTime() , end.getTime());
            fail("didn't catch start date before end date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }
    }
}
