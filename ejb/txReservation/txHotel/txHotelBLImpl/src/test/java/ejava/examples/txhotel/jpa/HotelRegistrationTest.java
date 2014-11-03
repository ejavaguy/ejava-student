package ejava.examples.txhotel.jpa;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import ejava.examples.txhotel.bl.InvalidReservationChangeException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

/** 
 * This class tests the ReservationDAO. The code contained here will be 
 * similar to the calls made by the Hotel Reservation business logic.
 *
 * @author jcstaff
 */
public class HotelRegistrationTest extends HotelDAOTestBase {
    
    /*
     * This will test the ability to create a new Reservation and Person
     */
	@Test
    public void testCreateRegistration() throws Exception {
        log.info("*** testCreateRegistration ***");
        Person person = new Person(0,0,"joe", "jones");
        Date startDate = 
            new Date(System.currentTimeMillis() + (60L*60L*24L*10L));
        Date endDate = 
            new Date(startDate.getTime() + (60L*60L*24L*2L));
        
        Reservation reservation = 
            reservationist.createReservation(person, startDate, endDate);
        
        assertNotNull("reservation was null", reservation);
        assertTrue("unexpected id" + reservation.getId(),
                reservation.getId() > 0);
        assertNotNull("unexpected confirmation:" + 
                reservation.getConfirmation(),
                reservation.getConfirmation());        
    }
    
	@Test
    public void testGoodCancelRegistration() throws Exception {
        log.info("*** testGoodCancelRegistration ***");
        Person person = new Person(0,0,"joe", "jones");

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+10);
        Date startDate = cal.getTime(); 
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+2);
        Date endDate = cal.getTime();
        
        Reservation reservation = 
            reservationist.createReservation(person, startDate, endDate);

        reservationist.cancelReservation(reservation);
    }      

	@Test
    public void testBadCancelRegistration() throws Exception {
        log.info("*** testBadCancelRegistration ***");
        Person person = new Person(0,0,"joe", "jones");

        Calendar cal = new GregorianCalendar();
        Date startDate = cal.getTime(); 
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+2);
        Date endDate = cal.getTime();
        
        Reservation reservation = 
            reservationist.createReservation(person, startDate, endDate);

        try {
            reservationist.cancelReservation(reservation);
            fail("bad cancel not reported");
        }
        catch (InvalidReservationChangeException ex) {
            log.debug("expected exception thrown for cancel:" + ex);
        }
    }
    
	@Test
    public void testGetRegistrations() throws Exception {
        log.info("*** testGetRegistrations ***");
        
        List<Reservation> reservations = new ArrayList<Reservation>();
        Person person = new Person(0,0,"joe", "jones");

        for(int i=0; i<10; i++) {
            Calendar cal = new GregorianCalendar();
            Date startDate = cal.getTime(); 
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+2);
            Date endDate = cal.getTime();
            reservations.add(
                reservationist.createReservation(person, startDate, endDate));
        }
        
        List<Reservation> reservations2 = 
            reservationist.getReservations(0, reservations.size());
        assertEquals("unexpected number of reservations:" + 
                reservations2.size(),
                reservations.size(), reservations2.size());
        
        for(Reservation r: reservations) {
            assertNotNull("reservation not found by confirmation",
                    r.getConfirmation());            
        }
    }      

	@Test
    public void testGetRegistrationsForName() throws Exception {
        log.info("*** testGetRegistrations ***");
        
        List<Reservation> reservations = new ArrayList<Reservation>();
        Person person1 = new Person(0,0,"joe", "jones");
        Person person2 = new Person(0,0,"jenny", "jones");

        for(int i=0; i<10; i++) {
            Calendar cal = new GregorianCalendar();
            Date startDate = cal.getTime(); 
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+2);
            Date endDate = cal.getTime();
            Person person = (i % 2 == 0) ? person1 : person2;
            reservations.add(
                reservationist.createReservation(person, startDate, endDate));
        }
        
        List<Reservation> reservations2 = 
            reservationist.getReservationsForPerson(
                    person1,0, reservations.size());
        assertEquals("unexpected number of reservations:" + 
                reservations2.size(),
                reservations.size()/2, reservations2.size());
    }      
}
