package info.ejava.examples.ejb.ejbjpa.dao;

import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;

import java.util.List;

public interface HotelDAO {
    void addFloor(Floor floor);
    Floor getFloor(int level);
    List<Floor> getFloors(int offset, int limit);
    
    Room getRoom(int number);
    List<Room> getAvailableRooms(Integer level, int offset, int limit);
    List<Room> getAvailableRoomsForUpdate(Integer level, int offset, int limit);
    Room findRoomByGuest(Guest guest);
    
    void addGuest(Guest guest);
    
    void populate();
    void clearAll();
    
    Floor fetchFloor(int level);
}
