package ejava.examples.txagent.bl;

import java.util.List;

import ejava.examples.txagent.bo.Booking;

public interface BookingAgent {
    Booking getBookingByConfirmation(String confirmation)
        throws AgentReservationException;
    List<Booking> getBookings(int index, int count)
        throws AgentReservationException;
    void cleanupBooking(String confirmation)
        throws AgentReservationException;
}
