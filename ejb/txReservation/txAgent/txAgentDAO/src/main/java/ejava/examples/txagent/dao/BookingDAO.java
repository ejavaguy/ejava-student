package ejava.examples.txagent.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.txagent.bo.Booking;

public interface BookingDAO {
    Booking createBooking(Booking booking)
        throws DAOException;
    Booking getBooking(long id)
        throws DAOException;    
    Booking getBookingByConfirmation(String confirmation)
        throws DAOException;    
    Booking updateBooking(Booking booking)
        throws DAOException;
    Booking removeBooking(Booking booking)
        throws DAOException;
    List<Booking> getBookings(int index, int count)
        throws DAOException;
    List<Booking> getBookings(String queryName, 
            Map<String, Object> params, int index, int count)
        throws DAOException;
}
