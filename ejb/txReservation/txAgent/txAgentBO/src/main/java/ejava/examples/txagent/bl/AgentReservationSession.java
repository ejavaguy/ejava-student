package ejava.examples.txagent.bl;

import java.util.Date;

import ejava.examples.txagent.bo.Booking;
import ejava.examples.txhotel.bo.Person;

public interface AgentReservationSession {
    void createBooking()
        throws AgentReservationException;
    void addReservation(Person person, Date startDate, Date endDate)
        throws AgentReservationException;
    void cancelBooking()
        throws AgentReservationException;
    Booking commit()
        throws AgentReservationException;
    void close();
}