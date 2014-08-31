package ejava.examples.txhotel.bl;

import java.util.Date;
import java.util.List;

import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

public interface HotelReservationSession {
    void createReservation(Person person, Date startDate, Date endDate)
        throws HotelReservationException;
    List<Reservation> commit()
        throws HotelReservationException;
    void cancelReservations()
        throws HotelReservationException;
    void close();
}
