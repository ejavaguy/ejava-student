package ejava.examples.txagent.blimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.BookingAgent;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txhotel.bl.HotelReservationException;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bo.Reservation;

public class AgentImpl implements BookingAgent {
    private static Logger log = LoggerFactory.getLogger(AgentImpl.class);
    private BookingDAO bookingDAO;
    private HotelReservationist reservationist;

    /**
     * Get reservations for stored IDs
     */
    protected void populateBooking(Booking booking) 
        throws AgentReservationException {
        for(String c: booking.getHotelConfirmations()) {
            try {
            	Reservation r = reservationist.getReservationByConfirmation(c);
                booking.addHotelReservation(r);
            } catch (HotelReservationException ex) {
                throw new AgentReservationException(
                        "error getting reservation by conf# " + c, ex);
            }
        }
    }
    
    public Booking getBookingByConfirmation(String confirmation) 
        throws AgentReservationException {
        Booking booking = bookingDAO.getBookingByConfirmation(confirmation);
        if (booking != null) {
            populateBooking(booking);
        }
        return booking;
    }

    public List<Booking> getBookings(int index, int count) 
        throws AgentReservationException {
        List<Booking> bookings = bookingDAO.getBookings(index, count);
        return bookings;
    }
    public void cleanupBooking(String confirmation) 
        throws AgentReservationException {
        log.debug("cleanup booking# " + confirmation);
        Booking booking = bookingDAO.getBookingByConfirmation(confirmation);
        if (booking != null) {
            bookingDAO.removeBooking(booking);
            for(String c: booking.getHotelConfirmations()) {
                try {
                    log.debug("cleaning up reservation # " + c);
                    reservationist.cleanupReservation(c);
                } catch (HotelReservationException ex) {
                    throw new AgentReservationException(
                            "unable to cancel reservation:" + ex);
                }
            }
        }
    }

    public void setBookingDAO(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }
    public void setReservationist(HotelReservationist reservationist) {
        this.reservationist = reservationist;
    }

}
