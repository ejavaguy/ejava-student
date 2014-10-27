package info.ejava.examples.ejb.ejbjpa.bo;

import static org.junit.Assert.*;

import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.HotelMgmt;
import info.ejava.examples.ejb.ejbjpa.bl.HotelMgmtImpl;
import info.ejava.examples.ejb.ejbjpa.bl.RoomUnavailableExcepton;
import info.ejava.examples.ejb.ejbjpa.dao.HotelDAO;
import info.ejava.examples.ejb.ejbjpa.dao.JPAHotelDAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotelTest {
    private static final Logger logger = LoggerFactory.getLogger(HotelTest.class);
    private static final String PU_NAME="ejbjpa-test";
    private static EntityManagerFactory emf;
    private EntityManager em;
    private HotelDAO dao;
    private HotelMgmt hotelMgmt;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory(PU_NAME);
    }
    
    @Before
    public void setUp() {
        em = emf.createEntityManager();
        
        dao = new JPAHotelDAO();
        ((JPAHotelDAO)dao).setEntityManager(em);
        
        hotelMgmt = new HotelMgmtImpl();
        ((HotelMgmtImpl)hotelMgmt).setHotelDao(dao);
        
        cleanup();
        populate();
        
        em.getTransaction().begin();
    }
    
    @After
    public void tearDown() {
        if (em!=null) {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                if (tx.getRollbackOnly()) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            }
            em.close(); em=null;
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (emf!=null) {
            emf.close(); emf=null;
        }
    }
    
    public void cleanup() {
        try {
            em.getTransaction().begin();
            dao.clearAll();
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }
    public void populate() {
        try {
            em.getTransaction().begin();
            dao.populate();
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }
    
    
    @Test
    public void hotel() {
        int offset=0;
        int limit=1;
        List<Floor> floors = hotelMgmt.getFloors(offset, limit);
        while (!floors.isEmpty()) {
            Floor floor = floors.get(0);
            logger.info("floor=" + floor);
            offset += limit;
            floors = hotelMgmt.getFloors(offset, limit);
        }
    }
    
    @Test
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
