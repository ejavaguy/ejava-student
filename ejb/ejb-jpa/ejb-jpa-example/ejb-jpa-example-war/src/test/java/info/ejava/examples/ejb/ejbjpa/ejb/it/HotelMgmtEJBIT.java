package info.ejava.examples.ejb.ejbjpa.ejb.it;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.RoomUnavailableExcepton;
import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;
import info.ejava.examples.ejb.ejbjpa.dto.FloorDTO;
import info.ejava.examples.ejb.ejbjpa.dto.RoomDTO;
import info.ejava.examples.ejb.ejbjpa.ejb.HotelInitRemote;
import info.ejava.examples.ejb.ejbjpa.ejb.HotelMgmtRemote;
import info.ejava.examples.ejb.ejbjpa.ejb.ReservationRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotelMgmtEJBIT  {
	private static final Logger logger = LoggerFactory.getLogger(HotelMgmtEJBIT.class);
    private static final String hotelmgmtJNDI = System.getProperty("hotelmgmt.jndi.name",
            "ejb:/ejb-jpa-example-war/HotelMgmtEJB!" + HotelMgmtRemote.class.getName());
    private static final String hotelinitJNDI = System.getProperty("hotelinit.jndi.name",
            "ejb:/ejb-jpa-example-war/HotelInitEJB!" + HotelInitRemote.class.getName());
    private static final String reservationJNDI = System.getProperty("reservation.jndi.name",
            "ejb:/ejb-jpa-example-war/ReservationEJB!" + ReservationRemote.class.getName() +"?stateful");
    private static final String hotelRlinitJNDI = System.getProperty("hotelRlinit.jndi.name",
            "ejb:/ejb-jpa-example-war/HotelInitResourceLocalEJB!" + HotelInitRemote.class.getName());
	private Context jndi;
	private HotelMgmtRemote hotelMgmt;
    private HotelInitRemote hotelInit;
    private HotelInitRemote hotelRlInit;
	
	@BeforeClass
	public static void setUpClass() throws NamingException {
	}
	
	@Before
	public void setUp() throws NamingException {
        jndi=new InitialContext();
        logger.debug("looking up jndi.name={}", hotelmgmtJNDI);
	    hotelMgmt = (HotelMgmtRemote)jndi.lookup(hotelmgmtJNDI);
        logger.debug("looking up jndi.name={}", hotelinitJNDI);
        hotelInit = (HotelInitRemote)jndi.lookup(hotelinitJNDI);
        logger.debug("looking up jndi.name={}", hotelRlinitJNDI);
        hotelRlInit = (HotelInitRemote)jndi.lookup(hotelRlinitJNDI);
        
        cleanup();
        populate();
	}
	
	private void cleanup() {
	    hotelInit.clearAll();
	}
	
	private void populate() {
	    hotelInit.populate();
	}
	
	/**
	 * This test uses the INIT EJB configured to use BMT and JTA
	 */
    @Test
    public void jta() {
        hotelInit.clearAll();
        List<Room> rooms = hotelMgmt.getAvailableRooms(null, 0, 0);
        assertEquals("available room not found", 0, rooms.size());
        
        hotelInit.populate();
        rooms = hotelMgmt.getAvailableRooms(null, 0, 0);
        assertEquals("available room not found", 4, rooms.size());
    }

    /**
     * This test uses the INIT EJB configured to use BMT and RESOURCE_LOCAL
     */
    @Test
	public void resourceLocal() {
	    hotelRlInit.clearAll();
        List<Room> rooms = hotelMgmt.getAvailableRooms(null, 0, 0);
        assertEquals("available room not found", 0, rooms.size());
	    
        hotelRlInit.populate();
        rooms = hotelMgmt.getAvailableRooms(null, 0, 0);
        assertEquals("available room not found", 4, rooms.size());
	}

	/**
	 * This test shows how proxy classes can be marshaled back to the client
	 * when returning a previously-managed entity as a DTO to the client.  
	 */
    @Test
    public void availableRooms() throws RoomUnavailableExcepton {
        List<Room> rooms = hotelMgmt.getAvailableRooms(null, 0, 1);
        logger.info("rooms collection type={}", rooms.getClass());
        assertEquals("available room not found", 1, rooms.size());
        Room room = rooms.get(0);
        logger.info("lets take: {}", room);
        logger.info("what floor class is this??? {}", room.getFloor().getClass());
        assertFalse("floor was not proxy", room.getFloor().getClass().equals(Floor.class));
        
        Guest guest = new Guest("Cosmo Kramer");
            //clear the room of Floor proxy class before returning
            //required if not running same version of Hibernate as server -- yuk!
        Floor floor = new Floor(room.getFloor().getLevel());
        room = new Room(floor, room.getNumber());
            //return clean Room POJO
        guest = hotelMgmt.checkIn(guest, room);
        logger.info("final guest: {}:{}", guest.getClass(), guest);
    }

    /**
     * This test shows that with some cleansing on the server-side, we can 
     * give the client pure POJOs if necessary.
     */
	@Test
	public void cleanAvailableRooms() throws RoomUnavailableExcepton {
	    List<Room> rooms = hotelMgmt.getCleanAvailableRooms(null, 0, 1);
        logger.info("rooms collection type={}", rooms.getClass());
	    assertEquals("available room not found", 1, rooms.size());
        Room room = rooms.get(0);
        logger.info("lets take:{}", room);
        logger.info("this looks like a good floor: {}", room.getFloor().getClass());
        assertTrue("floor was proxy", room.getFloor().getClass().equals(Floor.class));
        
        Guest guest = new Guest("Cosmo Kramer");
        guest = hotelMgmt.checkIn(guest, room);
        logger.info("final guest: {}:{}", guest.getClass(), guest);
	}

	/**
	 * This test shows how a parent with fetch=lazy children can cause the 
	 * client or any other code operating outside of the session with the DB
	 * will get an error when attempting to access the yet-to-be-loaded 
	 * associated objects.
	 */
	@Test
	public void getFloor() {
	    Floor floor = hotelMgmt.getFloor(0);
	    assertNotNull("floor not found", floor);
	    try {
	        logger.info("foor has {} rooms", floor.getRooms().size());
	        fail("did not get lazy-load exception");
	    } catch (LazyInitializationException expected) {
	        logger.info("got expected exception:{}", expected.toString());
	    }
	}
	
	/**
	 * This test shows how the code on the server-side could "touch" enough of 
	 * the returned object tree to cause lazy-loads to be resolved. This is 
	 * not very efficient, but it works. 
	 */
    @Test
    public void getTouchedFloor() {
        Floor floor = hotelMgmt.getTouchedFloor(0);
        assertNotNull("floor not found", floor);
        //this will cause lazy-load exception if rooms were not fetched
        logger.info("foor has {} rooms", floor.getRooms().size());
        for (Room room: floor.getRooms()) {
            //this will cause a lazy-load exception if occupant not fetched
            if (room.getOccupant()!=null) {
                logger.info("occupant.name={}", room.getOccupant().getName());
            }
        }
    }
    
    /**
     * This test shows an alternative approach to "touching" the managed 
     * entities before returning. In this case the DAO gets involved with a 
     * query that will more efficiently query for what is going to be returned.
     */
    @Test
    public void getFetchedFloor() {
        Floor floor = hotelMgmt.getFetchedFloor(0);
        assertNotNull("floor not found", floor);
        logger.info("foor has {} rooms", floor.getRooms().size());
        for (Room room: floor.getRooms()) {
            if (room.getOccupant()!=null) {
                logger.info("occupant.name={}", room.getOccupant().getName());
            }
        }
    }
    
    /**
     * This test shows that a server-side entity may not be the best abstraction
     * for a remote client. In this example, the client is trying to personally
     * locate an available room by looking at the occupant status of each room
     * on each floor. The server-side may have a good reason for holding onto the 
     * guest information for a room, but a normal remote client may only need to 
     * know the room is occupied.
     */
    @Test
    public void getOccupiedRoom() {
        //lets see if we can manually find a vacant room.....
        Floor floor = hotelMgmt.getFetchedFloor(0);
        //all floors have at least one occupant
        for (Room room: floor.getRooms()) {
            Guest occupant = room.getOccupant();
            if (occupant!=null) {
                logger.info("hey {}, are you done with room {} yet?", 
                        occupant.getName(), room.getNumber());
                //that is just rude
            }
        }
    }
    
    /**
     * This test demonstrates a solution to the above issue about unnecessary 
     * information. The floor and room are returned as types which obscure
     * information not meant for the client. Note that this likely also solves 
     * some of the lazy-load issues since creating this DTO likely "touched" every
     * entity it needed while still on the server-side.
     */
    @Test
    public void getOccupiedRoomDTO() {
        //lets see if we can manually find a vacant room.....
        FloorDTO floor = hotelMgmt.getFetchedFloorDTO(0);
        //all floors have at least one occupant
        for (RoomDTO room: floor.getRooms()) {
            if (room.isOccupied()) {
                logger.info("hey whoever, are you done with room {} yet?", room.getNumber());
                //still rude, but a bit more private
            }
        }
    }
    
    /**
     * This method demonstrates banging against a stateless EJB for each request.
     * If you look at the SQL/DB activity generated, the room information must be
     * pulled from the database on every call.
     * @throws NamingException
     * @throws RoomUnavailableExcepton
     */
    @Test
    public void statelessReservation() throws NamingException, RoomUnavailableExcepton {
        List<Room> availableRooms = hotelMgmt.getAvailableRooms(null, 0, 0);
        logger.debug("we have {} available rooms", availableRooms.size());

        List<Guest> members = new ArrayList<Guest>(availableRooms.size());
        int i=0;
        for (Room room: availableRooms) {
            Guest member = new Guest("member " + i++);
            member = hotelMgmt.checkIn(member, room);
            members.add(member);
        }
        
        logger.info("completed reservations for {} guests", members.size());
        int availableRooms2 = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.info("hotel has {} rooms available", availableRooms2);
        assertEquals("", availableRooms.size()-members.size(), availableRooms2);
    }
    
    
    /**
     * This test demonstrates how we can use a stateful session EJB to queue up
     * some requests on the server-side and then act on behalf of a single call
     * from the client. If you look behind the scenes, it also combines all the 
     * stateless EJB calls into a single persistence context instance. Unlike 
     * the statelessReservation you should notice the room information is pulled
     * from the database only once and re-used after it was made part of the initial
     * query.
     * @throws NamingException
     * @throws RoomUnavailableExcepton
     */
    @Test
    public void statefulReservation() throws NamingException, RoomUnavailableExcepton {
        int availableRooms = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.debug("we have {} available rooms", availableRooms);
        
        ReservationRemote checkin = (ReservationRemote) jndi.lookup(reservationJNDI);
        for (int i=0; i<availableRooms; i++) {
            Guest member = new Guest("member " + i);
            int count=checkin.addGuest(member);
            logger.debug("we have {} in our group so far", count);
        }
        List<Guest> guests = checkin.reserveRooms();
        logger.info("completed reservations for {} guests", guests.size());
        int availableRooms2 = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.info("hotel has {} rooms available", availableRooms2);
        assertEquals("", availableRooms-guests.size(), availableRooms2);
    }
    
    /**
     * This test demonstrates how we can stand a better chance of getting our reservation.
     * In this case when our stateful session EJB requests rooms for all guests --
     * it places a pessimistic lock on the room for the duration of the transaction.
     * If you look closely at the SQL/DB interaction -- you will see a select ... FOR UPDATE.
     * @throws NamingException 
     * @throws RoomUnavailableExcepton 
     */
    @Test
    public void pessimisticStatefulReservation() throws NamingException, RoomUnavailableExcepton {
        int availableRooms = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.debug("we have {} available rooms", availableRooms);
        
        ReservationRemote checkin = (ReservationRemote) jndi.lookup(reservationJNDI);
        for (int i=0; i<availableRooms; i++) {
            Guest member = new Guest("member " + i);
            int count=checkin.addGuest(member);
            logger.debug("we have {} in our group so far", count);
        }
        List<Guest> guests = checkin.reserveRoomsPessimistic();
        logger.info("completed reservations for {} guests", guests.size());
        int availableRooms2 = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.info("hotel has {} rooms available", availableRooms2);
        assertEquals("unexpected room count", availableRooms-guests.size(), availableRooms2);
    }
    
    /**
     * This test demonstrates how only the most recent reservation will get rejected
     * if every call before it is in its own transaction.
     * @throws RoomUnavailableExcepton 
     */
    @Test
    public void rollbackStateless() throws RoomUnavailableExcepton {
        List<Room> availableRooms = hotelMgmt.getAvailableRooms(null, 0, 0);
        logger.debug("we have {} available rooms", availableRooms.size());

        List<Guest> members = new ArrayList<Guest>(availableRooms.size());
        int i=0;
        for (Room room: availableRooms) {
            Guest member = new Guest("member " + i++);
            member = hotelMgmt.checkIn(member, room);
            members.add(member);
        }

        //try doing it again
        Room room = availableRooms.get(0);
        Guest member = new Guest("member " + i++);
        try {
            member = hotelMgmt.checkIn(member, room);
            members.add(member);
            fail("fail to detect bad checkin");
        } catch (RoomUnavailableExcepton ex) {
            logger.debug("expected exception making too many reservations:{}", ex.toString());
        }
        
        logger.info("completed reservations for {} guests", members.size());
        int availableRooms2 = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.info("hotel has {} rooms available", availableRooms2);
        assertEquals("unexpected room count", availableRooms.size()-members.size(), availableRooms2);
    }
    
    /**
     * This test demonstrates how all reservations are rolled back if any of them
     * fail.
     * @throws NamingException 
     */
    @Test
    public void rollbackStateful() throws NamingException {
        int availableRooms = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.debug("we have {} available rooms", availableRooms);
        
        ReservationRemote checkin = (ReservationRemote) jndi.lookup(reservationJNDI);
        for (int i=0; i<availableRooms+1; i++) {
            Guest member = new Guest("member " + i);
            int count=checkin.addGuest(member);
            logger.debug("we have {} in our group so far", count);
        }
        
        try {
            checkin.reserveRooms();
            fail("too many check-ins not detected");
        } catch (RoomUnavailableExcepton ex) {
            logger.debug("expected exception making too many reservations:{}", ex.toString());
        }
        
        int availableRooms2 = hotelMgmt.getAvailableRooms(null, 0, 0).size();
        logger.info("hotel has {} rooms available", availableRooms2);
        assertEquals("unexpected room count", availableRooms, availableRooms2);
    }
}
