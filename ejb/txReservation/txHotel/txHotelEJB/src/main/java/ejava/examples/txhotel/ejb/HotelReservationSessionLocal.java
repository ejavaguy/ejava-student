package ejava.examples.txhotel.ejb;

import javax.ejb.Local;

import ejava.examples.txhotel.bl.HotelReservationSession;

@Local
public interface HotelReservationSessionLocal extends HotelReservationSession {
}
