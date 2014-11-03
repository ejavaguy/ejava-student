package ejava.examples.txhotel.jpa;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.jpa.HotelDAOTestBase;

public class HotelReservationSessionTest extends HotelDAOTestBase {
    
	@Test
    public void testCreateReservations() throws Exception {
        log.info("*** testCreateReservations ***");
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<10; i++) {
            reservationSession.createReservation(
                    person, start.getTime(), end.getTime());
        }
        
        List<Reservation> mine = 
            reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(0,mine.size());
        
        List<Reservation> reservations = reservationSession.commit();
        assertEquals(10,reservations.size());
        
        mine = reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(10,mine.size());
    }
    
	@Test
    public void testCreateBadReservations() throws Exception {
        log.info("*** testCreateBadReservations ***");
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<9; i++) {
            reservationSession.createReservation(
                    person, start.getTime(), end.getTime());
        }
        //now create a bad one
        reservationSession.createReservation(
                person, end.getTime(), start.getTime());
        
        List<Reservation> mine = 
            reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(0,mine.size());
        
        try {
            reservationSession.commit();
            fail("invalid reservation wasn't detected");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
            em.flush(); //this won't do anything since we are rolling back
            em.getTransaction().rollback(); //un-write any updates
            em.getTransaction().begin(); //now go check for updates
        }
        
        mine = reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(0,mine.size());
    }      
}
