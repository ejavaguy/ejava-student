package info.ejava.examples.ejb.ejbjpa.ejb;

import java.util.ArrayList;
import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.HotelMgmt;
import info.ejava.examples.ejb.ejbjpa.bl.HotelMgmtImpl;
import info.ejava.examples.ejb.ejbjpa.bl.RoomUnavailableExcepton;
import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;
import info.ejava.examples.ejb.ejbjpa.dao.HotelDAO;
import info.ejava.examples.ejb.ejbjpa.dao.JPAHotelDAO;
import info.ejava.examples.ejb.ejbjpa.dto.FloorDTO;
import info.ejava.examples.ejb.ejbjpa.dto.RoomDTO;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class HotelMgmtEJB implements HotelMgmtRemote, HotelMgmtLocal {
    private static final Logger logger = LoggerFactory.getLogger(HotelMgmtEJB.class);

    @PersistenceContext(unitName="ejbjpa-hotel")
    private EntityManager em;
    private HotelDAO dao;                                                                                                                                                                                                                                                                                                                                   
    private HotelMgmt hotelMgmt; 
    
    @PostConstruct
    public void init() {
        logger.debug("*** HotelMgmtEJB({}):init ***", super.hashCode());
        dao = new JPAHotelDAO();
        ((JPAHotelDAO)dao).setEntityManager(em);
        
        hotelMgmt = new HotelMgmtImpl();
        ((HotelMgmtImpl)hotelMgmt).setHotelDao(dao);
    }

    @PreDestroy
    public void destroy() {
        logger.debug("*** HotelMgmtEJB({}):destroy ***", super.hashCode());
    }

    @Override
    public Room getRoom(int number) {
        return hotelMgmt.getRoom(number);
    }

    @Override
    public Floor getFloor(int level) {
        return hotelMgmt.getFloor(level);
    }

    @Override
    public Floor getTouchedFloor(int level) {
        Floor floor = getFloor(level);
        /*
     select
         floor0_.LEVEL as LEVEL1_0_0_ 
     from
         EJBJPA_FLOOR floor0_ 
     where
         floor0_.LEVEL=?
         */
        if (floor!=null) {
            //touch the managed-floor to cause lazy-loads to be resolved            
            floor.getRooms().isEmpty();
            /*
     select
         rooms0_.FLOOR_ID as FLOOR_ID2_0_0_,
         rooms0_.ROOM_NUMBER as ROOM_NUM1_2_0_,
         rooms0_.ROOM_NUMBER as ROOM_NUM1_2_1_,
         rooms0_.FLOOR_ID as FLOOR_ID2_2_1_,
         rooms0_.occupant_GUEST_ID as occupant3_2_1_ 
     from
         EJBJPA_ROOM rooms0_ 
     where
         rooms0_.FLOOR_ID=? 
     order by
         rooms0_.ROOM_NUMBER
             */
            for (Room room: floor.getRooms()) {
                Guest guest = room.getOccupant();
                if (guest!=null) {
                    guest.getName(); //touch all occupants to cause lazy-loads to be resolved
                    /*
     select
         guest0_.GUEST_ID as GUEST_ID1_1_0_,
         guest0_.name as name2_1_0_ 
     from
         EJBJPA_GUEST guest0_ 
     where
         guest0_.GUEST_ID=?

     select
         guest0_.GUEST_ID as GUEST_ID1_1_0_,
         guest0_.name as name2_1_0_ 
     from
         EJBJPA_GUEST guest0_ 
     where
         guest0_.GUEST_ID=?
                     */
                }
            }
        }
        return floor;
    }

    @Override
    public Floor getFetchedFloor(int level) {
        return dao.fetchFloor(level);
        /*
     select
         floor0_.LEVEL as LEVEL1_0_0_,
         rooms1_.ROOM_NUMBER as ROOM_NUM1_2_1_,
         guest2_.GUEST_ID as GUEST_ID1_1_2_,
         rooms1_.FLOOR_ID as FLOOR_ID2_2_1_,
         rooms1_.occupant_GUEST_ID as occupant3_2_1_,
         rooms1_.FLOOR_ID as FLOOR_ID2_0_0__,
         rooms1_.ROOM_NUMBER as ROOM_NUM1_2_0__,
         guest2_.name as name2_1_2_ 
     from
         EJBJPA_FLOOR floor0_ 
     inner join
         EJBJPA_ROOM rooms1_ 
             on floor0_.LEVEL=rooms1_.FLOOR_ID 
     inner join
         EJBJPA_GUEST guest2_ 
             on rooms1_.occupant_GUEST_ID=guest2_.GUEST_ID 
     where
         floor0_.LEVEL=? 
     order by
         rooms1_.ROOM_NUMBER
         */
    }
    
    @Override
    public FloorDTO getFetchedFloorDTO(int level) {        
        Floor floor = getFloor(level);
        /*
    select 
        floor0_.LEVEL as LEVEL1_0_0_ 
    from
        EJBJPA_FLOOR floor0_  
    where
        floor0_.LEVEL=? 
         */
        return toDTO(floor);
        /*
    select 
        rooms0_.FLOOR_ID as FLOOR_ID2_0_0_, 
        rooms0_.ROOM_NUMBER as ROOM_NUM1_2_0_,
        rooms0_.ROOM_NUMBER as ROOM_NUM1_2_1_,
        rooms0_.FLOOR_ID as FLOOR_ID2_2_1_, 
        rooms0_.occupant_GUEST_ID as occupant3_2_1_ 
    from
        EJBJPA_ROOM rooms0_   
    where
        rooms0_.FLOOR_ID=? 
    order by
        rooms0_.ROOM_NUMBER
         */
    }
    
    
    private FloorDTO toDTO(Floor floor) {
        if (floor==null) { return null; }
        FloorDTO floorDTO = new FloorDTO(floor.getLevel());
        if (floor.getRooms()!=null) { for (Room room: floor.getRooms()) {
            floorDTO.withRoom(toDTO(room));
        }}
        return floorDTO;
    }
    
    private RoomDTO toDTO(Room room) {
        if (room==null) { return null; }
        RoomDTO roomDTO = new RoomDTO(room.getNumber());
        //remote client shouldn't care who is in the room -- just if busy
        roomDTO.setOccupied(room.getOccupant()!=null);
        return roomDTO;
    }

    @Override
    public List<Floor> getFloors(int offset, int limit) {
        return hotelMgmt.getFloors(offset, limit);
    }

    @Override
    public List<Room> getAvailableRooms(Integer level, int offset, int limit) {
        return hotelMgmt.getAvailableRooms(level, offset, limit);
    }

    @Override
    public List<Room> getCleanAvailableRooms(Integer level, int offset, int limit) {
        List<Room> rooms = getAvailableRooms(level, offset, limit);
        return toClean(rooms);
    }

    /**
     * This helper method will instantiate new entity classes to re-use as DTOs.
     * This is done to remove hibernate-proxy classes that are part of the managed 
     * entity.
     */
    private List<Room> toClean(List<Room> rooms) {
        if (rooms==null) { return null; }
        List<Room> cleanRooms = new ArrayList<Room>(rooms.size());
        for (Room room : rooms) {
            Floor floor = room.getFloor();
            Floor cleanFloor = new Floor(floor.getLevel());
            Room cleanRoom = new Room(cleanFloor, room.getNumber());            
            cleanFloor.withRoom(cleanRoom);
            Guest occupant = room.getOccupant();
            if (occupant!=null) {
                Guest cleanOccupant = new Guest(occupant.getId());
                cleanOccupant.setName(occupant.getName());
                cleanRoom.setOccupant(cleanOccupant);
            }
            cleanRooms.add(cleanRoom);
        }
        return cleanRooms;
    }
    
    /**
     * This local-interface-only method places a pessimistic write-lock on each 
     * returned room.
     */
    @Override
    public List<Room> getAvailableRoomsForUpdate(Integer level, int offset, int limit) {
        return dao.getAvailableRoomsForUpdate(level, offset, limit);
    }

    @Override
    public Guest checkIn(Guest guest, Room room) throws RoomUnavailableExcepton {
        logger.debug("checkin(guest={}, room={})", guest, room);
        return hotelMgmt.checkIn(guest, room);
    }

    @Override
    public int checkout(Guest guest) {
        return hotelMgmt.checkout(guest);
    }
}
