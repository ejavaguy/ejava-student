package ejava.examples.txhotel.bl;

import java.util.Date;
import java.util.List;

import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

public interface HotelReservationist {
    Reservation createReservation(Person person, Date startDate, Date endDate)
        throws HotelReservationException;
    Reservation commitReservation(Reservation reservation)
        throws HotelReservationException;
    List<Reservation> getReservationsForPerson(Person person, 
            int index, int count)
        throws HotelReservationException;
    Reservation getReservationByConfirmation(String confirmation) 
        throws HotelReservationException;
    List<Reservation> getReservations(int index, int count)
        throws HotelReservationException;
    void cancelReservation(Reservation reservation)
        throws HotelReservationException;
    //used to cleanup for testing; shouldn't really be here
    void cleanupReservation(String confirmation)
        throws HotelReservationException;
}
