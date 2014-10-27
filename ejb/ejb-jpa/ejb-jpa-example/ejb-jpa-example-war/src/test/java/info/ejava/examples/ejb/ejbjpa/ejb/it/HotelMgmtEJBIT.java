package info.ejava.examples.ejb.ejbjpa.ejb.it;

import static org.junit.Assert.*;

import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.RoomUnavailableExcepton;
import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;
import info.ejava.examples.ejb.ejbjpa.ejb.HotelInitRemote;
import info.ejava.examples.ejb.ejbjpa.ejb.HotelMgmtRemote;

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
	private Context jndi;
	private HotelMgmtRemote hotelMgmt;
    private HotelInitRemote hotelInit;
	
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
        logger.info("what's floor is this??? {}", room.getFloor().getClass());
        assertFalse("floor was not proxy", room.getFloor().getClass().equals(Floor.class));
        
        Guest guest = new Guest("Cosmo Kramer");
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
	        logger.info("got expected exception:{}", expected);
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
	
	
//	@Test
    public void hotel() {
        int offset=0;
        int limit=1;
        Room room = hotelMgmt.getRoom(1);
        assertNotNull("room not found", room);
        List<Floor> floors = hotelMgmt.getFloors(offset, limit);
        while (!floors.isEmpty()) {
            Floor floor = floors.get(0);
            logger.info("floor=" + floor);
            offset += limit;
            floors = hotelMgmt.getFloors(offset, limit);
        }
    }
	
//	@Test
	public void stay() throws RoomUnavailableExcepton {
        List<Room> rooms = hotelMgmt.getAvailableRooms(2, 0, 1);
        assertEquals("unexpected rooms.count", 1, rooms.size());
        Room room = rooms.get(0);
        assertEquals("unexpected floor", 2, room.getFloor().getLevel());
        assertNull("room occupied", room.getOccupant());
        Guest guest = new Guest("Cosmo Kramer");
        guest = hotelMgmt.checkIn(guest, room);
        
        Room hotelRoom = hotelMgmt.getRoom(room.getNumber());
        assertNotNull("room not occupied", hotelRoom.getOccupant());
        logger.debug("{}", room);
        
        int count = hotelMgmt.checkout(guest);
        assertEquals("unexpected count", 1, count);
	}
}
