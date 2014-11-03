package ejava.examples.txhotel.ejb;

import javax.ejb.Remote;

import ejava.examples.txhotel.bl.HotelReservationist;

@Remote
public interface HotelRegistrationRemote extends HotelReservationist {

}
