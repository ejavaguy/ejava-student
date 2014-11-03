package info.ejava.examples.ejb.ejbjpa.bl;

import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;
import info.ejava.examples.ejb.ejbjpa.dao.HotelDAO;

public class HotelMgmtImpl implements HotelMgmt {
    private HotelDAO dao;
    
    public void setHotelDao(HotelDAO dao) {
        this.dao = dao;
    }

    @Override
    public Room getRoom(int number) {
        return dao.getRoom(number);
    }

    @Override
    public Floor getFloor(int level) {
        return dao.getFloor(level);
    }

    @Override
    public List<Floor> getFloors(int offset, int limit) {
        return dao.getFloors(offset, limit);
    }
    
    @Override
    public List<Room> getAvailableRooms(Integer level, int offset, int limit) {
        return dao.getAvailableRooms(level, offset, limit);
    }
    
    @Override
    public Guest checkIn(Guest guest, Room room) throws RoomUnavailableExcepton {
        if (room==null) {
            throw new RoomUnavailableExcepton("no room supplied");
        }
        
        Room hotelRoom = dao.getRoom(room.getNumber());
        if (hotelRoom==null) {
            throw new RoomUnavailableExcepton(String.format("room [%d] does not exist", room.getNumber()));
        }
        
        if (hotelRoom.getOccupant()!=null) {
            throw new RoomUnavailableExcepton(String.format("room is occupied by %s", hotelRoom.getOccupant()));
        }
        
        dao.addGuest(guest);
        hotelRoom.setOccupant(guest);
        return guest;
    }
    
    @Override
    public int checkout(Guest guest) {
        Room room=dao.findRoomByGuest(guest);
        if (room!=null) {
            room.setOccupant(null);
            return 1;
        }
        return 0;
    }
}
