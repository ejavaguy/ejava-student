package info.ejava.examples.ejb.ejbjpa.ejb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.RoomUnavailableExcepton;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateful
public class ReservationEJB implements ReservationRemote {
    private static final Logger logger = LoggerFactory.getLogger(ReservationEJB.class);
    
    @PersistenceContext(unitName="ejbjpa-hotel")
    private EntityManager em;
    
    @EJB
    private HotelMgmtLocal hotelMgmt;
    
    @Resource
    private SessionContext ctx;
    
    List<Guest> guests = new LinkedList<Guest>();

    @PostConstruct
    public void init() {
        logger.debug("*** ReservationEJB({}):init ***", super.hashCode());
    }
    
    @PreDestroy
    public void destroy() {
        logger.debug("*** ReservationEJB({}):destroy ***", super.hashCode());
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int addGuest(Guest guest) {
        logger.debug("em={}", em);
        if (guest!=null) {
            guests.add(guest);
        }
        return guests.size();
    }

    protected List<Guest> reserveRooms(List<Room> rooms) throws RoomUnavailableExcepton {
        if (rooms.size() < guests.size()) {
            //no rollback needed, we didn't do anything
            throw new RoomUnavailableExcepton(String.format("found on %d out of %d required", 
                    rooms.size(), guests.size()));
        }

        //assign each one of them a room
        List<Guest> completed = new ArrayList<Guest>(guests.size());
        Iterator<Room> roomItr = rooms.iterator();
        for (Guest guest: guests) {
            Room room = roomItr.next();
            try {
                //the room could be unavailable -- depending on whether pessimistic lock created
                logger.debug("stateful.em contains(guest) before checkin={}", em.contains(guest));
                guest = hotelMgmt.checkIn(guest, room);
                logger.debug("stateful.em contains(guest) after checkin={}", em.contains(guest));
                completed.add(guest);
            } catch (RoomUnavailableExcepton ex) {
                //rollback any previous reservations
                ctx.setRollbackOnly();
                throw ex;
            }
        }
        return completed;
    }
    
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Remove
    public List<Guest> reserveRooms() throws RoomUnavailableExcepton {
        //do not get any locks on each room prior to making changes 
        List<Room> rooms = hotelMgmt.getAvailableRooms(null, 0, guests.size());
        return reserveRooms(rooms);
    }
    
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Remove
    public List<Guest> reserveRoomsPessimistic() throws RoomUnavailableExcepton {
        //get a pessimistic lock on each room prior to making changes 
        List<Room> rooms = hotelMgmt.getAvailableRoomsForUpdate(null, 0, guests.size());
        return reserveRooms(rooms);
    }
}
