package ejava.examples.txhotel.blimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txhotel.bl.HotelReservationException;
import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

/**
 * This class represents some stateful business logic that caches desired
 * reservations until the caller calls commit. At that time they will all
 * be added to the hotel using the stateless reservationist.
 */
public class HotelReservationSessionImpl implements HotelReservationSession {
    private static final Log log = 
        LogFactory.getLog(HotelReservationSessionImpl.class);
    private List<Reservation> pending = new ArrayList<Reservation>();
    private HotelReservationist reservationist;

    public void createReservation(Person person, Date startDate, Date endDate)
        throws HotelReservationException {
        pending.add(new Reservation(0,0,null,person, startDate, endDate));        
    	log.debug("added pending reservation, size=" + pending.size());
    }
    
    public void cancelReservations() throws HotelReservationException {
    }

    public List<Reservation> commit() throws HotelReservationException {
        log.info("************ creating " + pending.size() + " reservations ***");
        List<Reservation> commited = new ArrayList<Reservation>();
        for(Reservation p: pending) {
            Reservation c = reservationist.createReservation(
                    p.getPerson(), p.getStartDate(), p.getEndDate());
            log.debug("created reservation:" + c);
            commited.add(c);
        }
        log.debug("returning " + commited.size() + " reservations");
        return commited;
    }

    public void setReservationist(HotelReservationist reservationist) {
        this.reservationist = reservationist;
    }
    
    @Override
    public void close() {
    }
}
