package info.ejava.examples.ejb.ejbjpa.ejb;

import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.HotelMgmt;
import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Room;
import info.ejava.examples.ejb.ejbjpa.dto.FloorDTO;

import javax.ejb.Remote;

@Remote
public interface HotelMgmtRemote extends HotelMgmt {
    /**
     * This method will clear the previous-managed entity instances of any 
     * provider proxies.
     * @param level
     * @param offser
     * @param limit
     * @return rooms
     */
    List<Room> getCleanAvailableRooms(Integer level, int offser, int limit);

    /**
     * This method will make sure the returned parent object is fully loaded
     * by explicitly "touching" enough of the parent and children entities
     * and collections to cause them all to be EAGERly loaded.
     * @param level
     * @return floor
     * 
     * 
     */
    Floor getTouchedFloor(int level);

    /**
     * This method will make sure the returned parent object is fully loaded
     * by "fetch"ing all required children as part of the DAO query.
     * @param level
     * @return floor
     */
    Floor getFetchedFloor(int level);

    /**
     * This method will return a slightly-washed down set of data objects to 
     * the caller with sensitive or non-pertinent information not included in 
     * the abstraction.
     * @param level
     * @return floor
     */
    FloorDTO getFetchedFloorDTO(int level);
}
