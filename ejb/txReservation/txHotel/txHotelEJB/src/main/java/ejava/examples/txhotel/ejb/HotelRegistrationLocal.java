package ejava.examples.txhotel.ejb;

import javax.ejb.Local;

import ejava.examples.txhotel.bl.HotelReservationist;

@Local
public interface HotelRegistrationLocal extends HotelReservationist {

}
