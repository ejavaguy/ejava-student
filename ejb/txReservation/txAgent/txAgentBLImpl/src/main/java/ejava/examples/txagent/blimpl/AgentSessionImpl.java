package ejava.examples.txagent.blimpl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.AgentReservationSession;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txhotel.bl.HotelReservationException;
import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

public class AgentSessionImpl implements AgentReservationSession {
    private static final Logger log = LoggerFactory.getLogger(AgentSessionImpl.class);
    private Booking booking = new Booking();
    private BookingDAO bookingDAO;
    private HotelReservationSession reservationist;
    private long counter = 0L;
    
    public void createBooking() throws AgentReservationException {
    }

    public void addReservation(Person person, Date startDate, Date endDate) 
        throws AgentReservationException {
        try {
        	log.debug("creating reservation in stateful session bean");
            reservationist.createReservation(person, startDate, endDate);
        } 
        catch (InvalidParameterException ex) {
            throw new ejava.examples.txagent.bl.InvalidParameterException(
                    "hotel reported invalid parameters", ex);
        }
        catch (HotelReservationException ex2) {
            throw new AgentReservationException(
                    "error adding hotel reservation", ex2);
        }
    }

    public void cancelBooking() throws AgentReservationException {
        try {
            reservationist.cancelReservations();
        } 
        catch (HotelReservationException ex2) {
            throw new AgentReservationException(
                    "error cancelling hotel reservation", ex2);
        }
    }

    public Booking commit() throws AgentReservationException {
        try {
            log.debug("committing booking: " + booking);
            String confirmation = 
                new Long(System.currentTimeMillis()).toString() + "-" + 
                ++counter;
            booking.setConfirmation(confirmation);
            bookingDAO.createBooking(booking);
            log.debug("committing hotels for booking");
            List<Reservation> reservations = reservationist.commit();
            log.debug(String.format("commit complete, adding %d hotel reservations to booking",
            		reservations.size()));
            for (Reservation r: reservations) {
                booking.addHotelReservation(r);
            }
            log.debug("completed booking:" + booking);
            return booking;
        } 
        catch (HotelReservationException ex) {
            log.info("error commiting booking, hotel reported issue:" + ex);
            throw new AgentReservationException(
                    "error committing booking", ex);
        }
        finally {
            //reservationist.close();        	
        }
    }
    @Override
    public void close() {
    }
    public void setBookingDAO(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }
    public void setReservationist(HotelReservationSession reservationist) {
        this.reservationist = reservationist;
    }
}
