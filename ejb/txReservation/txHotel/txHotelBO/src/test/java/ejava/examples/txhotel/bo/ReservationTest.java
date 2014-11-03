package ejava.examples.txhotel.bo;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class ReservationTest extends TestCase {
    Log log = LogFactory.getLog(ReservationTest.class);
    
    public void testPerson() {
        log.info("*** testPerson ***");
        Person person = new Person();
        log.debug("created person:" + person);
        assertEquals(0L, person.getId());
        assertEquals(0L, person.getVersion());
        assertNull(person.getFirstName());
        assertNull(person.getFirstName());
        
        long version = 12;
        person.setVersion(version);
        String firstName = "joe";
        person.setFirstName(firstName);
        String lastName = "smith";
        person.setLastName(lastName);
        log.debug("updated person:" + person);
        
        Person person2 = new Person(0,version, firstName, lastName);
        log.debug("created second person:" + person2);
        
        assertEquals("ids did not match", person.getId(), person2.getId());
        assertEquals("versions did not match", 
                person.getVersion(), person2.getVersion());
        assertEquals("firstName did not match", 
                person.getFirstName(), person2.getFirstName());
        assertEquals("lastName did not match", 
                person.getLastName(), person2.getLastName());
    }
    
    public void testReservation() {
        log.info("*** testReservation ***");
        
        Reservation reservation = new Reservation();
        log.debug("created reservation:" + reservation);
        assertEquals(0, reservation.getId());
        assertNull(reservation.getConfirmation());
        assertNull(reservation.getStartDate());
        assertNull(reservation.getEndDate());
        assertNull(reservation.getPerson());
        
        long version = 35;
        reservation.setVersion(version);
        String confirmation = "1234";
        reservation.setConfirmation(confirmation);
        Date startDate = new Date(System.currentTimeMillis());
        reservation.setStartDate(startDate);
        Date endDate = new Date(System.currentTimeMillis()+(60L*60L*24L));
        reservation.setEndDate(endDate);
        Person person = new Person(0,0, "joe", "smith");
        reservation.setPerson(person);
        log.debug("updated reservation:" + reservation);
        
        Reservation reservation2 = new Reservation(
                0, version, confirmation, person, startDate, endDate);
        log.debug("created second reservation:" + reservation2);
        
        assertEquals("ids didn't match", 
                reservation.getId(), reservation2.getId());
        assertEquals("versions didn't match", 
                reservation.getVersion(), reservation2.getVersion());
        assertEquals("conf# didn't match", 
                reservation.getConfirmation(), reservation2.getConfirmation());
        assertEquals("startDates didn't match", 
                reservation.getStartDate().getTime(), 
                reservation2.getStartDate().getTime());
        assertEquals("endDates didn't match",
                reservation.getEndDate(),
                reservation2.getEndDate());
    }

}
