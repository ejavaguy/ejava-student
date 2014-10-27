package info.ejava.examples.ejb.ejbjpa.ejb;

import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.HotelMgmt;
import info.ejava.examples.ejb.ejbjpa.bo.Room;

import javax.ejb.Local;

@Local
public interface HotelMgmtLocal extends HotelMgmt {
    List<Room> getAvailableRoomsForUpdate(Integer level, int offset, int limit);
}
