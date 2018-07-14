package ejava.examples.txagent.bo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txagent.bo.Booking;

import junit.framework.TestCase;

public class AgentTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(AgentTest.class);

    public void testBooking() {
        log.info("*** testBooking ***");
        
        Booking booking = new Booking();
        booking.setConfirmation("1");
        Person person = new Person(0,0,"joe", "jones");
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, 10);
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 2);
        Date end = cal.getTime();
        Reservation reservation = 
            new Reservation(0,0, "1234", person, start, end);
        booking.addHotelReservation(reservation);
        log.debug("created booking:" + booking);
        
        assertEquals(reservation.getConfirmation(),
                booking.getHotelConfirmations().iterator().next());
    }
}
