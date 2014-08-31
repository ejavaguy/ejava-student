package ejava.examples.txhotel.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.txhotel.bo.Reservation;

public interface ReservationDAO {
    Reservation createReservation(Reservation reservation)
        throws DAOException;
    Reservation getReservation(long id)
        throws DAOException;    
    Reservation getReservationByConfirmation(String confirmation)
        throws DAOException;    
    Reservation updateReservation(Reservation reservation)
        throws DAOException;
    Reservation removeReservation(Reservation reservation)
        throws DAOException;
    List<Reservation> getReservations(int index, int count)
        throws DAOException;
    List<Reservation> getReservations(String queryName, 
            Map<String, Object> params, int index, int count)
        throws DAOException;
}
