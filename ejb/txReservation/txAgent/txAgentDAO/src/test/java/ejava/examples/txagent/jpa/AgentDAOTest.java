package ejava.examples.txagent.jpa;

import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

import ejava.examples.txagent.bo.Booking;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

/** 
 * This class tests the ReservationDAO. The code contained here will be 
 * similar to the calls made by the Hotel Reservation business logic.
 *
 */
public class AgentDAOTest extends AgentDAOTestBase {
    
    /*
     * This will test the ability to create a new Reservation and Person
     */
	@Test
    public void testCreateBooking() {
        log.info("*** testCreateBooking ***");
        
        Reservation reservation = new Reservation();
        reservation.setStartDate(new Date(System.currentTimeMillis()));
        reservation.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation.setPerson(new Person(0,0,"joe", "smith"));
        Booking booking = new Booking();
        booking.addHotelReservation(reservation);
        booking.setConfirmation("1");
                       
        booking = bookingDAO.createBooking(booking);
        assertTrue("booking id not assigned", booking.getId()!=0);
    }

	@Test
    public void testGetBooking() {
        log.info("*** testGetBooking ***");

        Person person1 = new Person(0,0,"joe", "smith");
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);
        Booking booking = new Booking();
        booking.addHotelReservation(reservation1);
        booking.setConfirmation("1");
                       
        booking = bookingDAO.createBooking(booking);
        Booking booking2 = bookingDAO.getBooking(
                booking.getId());
        assertNotNull(booking2);
    }

	@Test
    public void testRemoveBooking() {
        log.info("*** testRemoveBooking ***");

        Person person1 = new Person(0,0,"joe", "smith");
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);
        Booking booking = new Booking();
        booking.addHotelReservation(reservation1);
        booking.setConfirmation("1");
                       
        booking = bookingDAO.createBooking(booking);
        
        List<Booking> bookings1 = bookingDAO.getBookings(0,100);

        bookingDAO.removeBooking(booking);
        
        List<Booking> booking2 = bookingDAO.getBookings(0,100);
        assertEquals("unexpected number of bookings:" + booking2.size(),
                bookings1.size()-1, booking2.size());
    }
    
	@Test
    public void testQueryByConfirmation() {
        log.info("*** testByConfirmation ***");

        Person person1 = new Person(0,0,"joe", "smith");
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);                       
        Booking booking = new Booking();
        booking.addHotelReservation(reservation1);
        booking.setConfirmation("1");
        booking = bookingDAO.createBooking(booking);
        
        bookingDAO.createBooking(new Booking(0,0,"2"));
        
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("confirmation", booking.getConfirmation());
        List<Booking> bookings1 = bookingDAO.getBookings(
                "getBookingByConfirmation", params, 0, 100);
        assertEquals("unexpected number of bookings:" + bookings1.size(),
                1, bookings1.size());
        
        //we later added a convenience method in DAO for this query
        Booking booking3 = 
            bookingDAO.getBookingByConfirmation(booking.getConfirmation());
        assertNotNull("booking not found by confirmation", booking3);


        params.put("confirmation", "fake");
        bookings1 = bookingDAO.getBookings(
                "getBookingByConfirmation", params, 0, 100);
        assertEquals("unexpected number of bookings:" + bookings1.size(),
                0, bookings1.size());
    }
}
