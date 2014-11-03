package info.ejava.examples.ejb.ejbjpa.bl;

import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;

public interface HotelMgmt {
    Room getRoom(int number);
    Floor getFloor(int level);
    List<Floor> getFloors(int offset, int limit);
    List<Room> getAvailableRooms(Integer level, int offset, int limit);
    Guest checkIn(Guest guest, Room room) throws RoomUnavailableExcepton;
    int checkout(Guest guest);
}
