package ejava.examples.txhotel.blimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.txhotel.bl.HotelReservationException;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bl.InvalidReservationChangeException;
import ejava.examples.txhotel.bl.ReservationNotFoundException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.dao.ReservationDAO;

public class HotelReservationImpl implements HotelReservationist {
    private static final Logger log = LoggerFactory.getLogger(HotelReservationImpl.class);
    protected ReservationDAO dao;
    private static long counter=0;

    protected Reservation getReservation(String confirmation) 
        throws HotelReservationException {        
    
        Reservation reservation = 
            dao.getReservationByConfirmation(confirmation);
        if (reservation == null) {
            throw new ReservationNotFoundException(
                    "unable to locate reservation for conf#" + 
                    confirmation);
        }
        return reservation;
    }

    public void cancelReservation(Reservation reservation) 
        throws HotelReservationException {        
        
        Reservation officialCopy = 
            getReservation(reservation.getConfirmation()); 
                    
        Calendar cutoff = new GregorianCalendar();
        cutoff.setTime(officialCopy.getStartDate());
        cutoff.add(Calendar.DAY_OF_YEAR, -2);
        
        Calendar now = new GregorianCalendar();
        
        log.debug("comparing now=" + now.getTime() +
                ", cutoff=" + cutoff.getTime() +
                "=" + now.compareTo(cutoff));
        
        if (now.compareTo(cutoff) > 0) {
            throw new InvalidReservationChangeException(
                    "unable to cancel reservation, " +
                    "start date to close or has passed:" + 
                    officialCopy.getStartDate());
        }
        
        dao.removeReservation(officialCopy);        
    }

    public Reservation commitReservation(Reservation reservation) 
        throws HotelReservationException {
        
        log.info("nothing implemented yet");
        return dao.getReservation(reservation.getId());
    }

    public Reservation createReservation(
            Person person, Date startDate, Date endDate) 
            throws HotelReservationException {
        
        log.debug("checking dates; start=" + startDate + ", end=" + endDate);
        if (startDate == null) {
            throw new InvalidParameterException("no start date supplied");
        }
        else if (endDate == null) {
            throw new InvalidParameterException("no end date supplied");
        }
        else if (startDate.getTime() > endDate.getTime()) {
            throw new InvalidParameterException("start date after end date");
        }
        
        String confirmation = new Long(
                System.currentTimeMillis()).toString() + "-" + ++counter;
        Reservation reservation = new Reservation(
                0, 0, confirmation, person, startDate, endDate);
        return dao.createReservation(reservation);
    }

    public List<Reservation> getReservations(int index, int count) 
        throws HotelReservationException {
        return dao.getReservations(index, count);
    }

    public List<Reservation> getReservationsForPerson(
            Person person, int index, int count) 
            throws HotelReservationException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("firstName", person.getFirstName());
        params.put("lastName", person.getLastName());
        return dao.getReservations("getReservationsByName", 
                params, index, count);
    }
    //this is a cleanup method for remote testing; bypasses cancel BL
    public void cleanupReservation(String confirmation) 
        throws HotelReservationException {
        log.debug("cleanup reservation# " + confirmation);
        Reservation reservation = getReservation(confirmation);
        if (reservation != null) {
            dao.removeReservation(reservation);
        }
    }


    public void setDao(ReservationDAO dao) {
        this.dao = dao;
    }

    public Reservation getReservationByConfirmation(String confirmation) 
        throws HotelReservationException {
        return getReservation(confirmation);
    }
}
