package ejava.examples.txhotel.ejb;

import javax.ejb.Remote;

import ejava.examples.txhotel.bl.HotelReservationSession;

@Remote
public interface HotelReservationSessionRemote extends HotelReservationSession {
}
