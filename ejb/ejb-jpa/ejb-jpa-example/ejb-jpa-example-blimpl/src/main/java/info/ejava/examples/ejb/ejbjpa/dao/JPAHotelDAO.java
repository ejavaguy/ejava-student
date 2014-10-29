package info.ejava.examples.ejb.ejbjpa.dao;

import info.ejava.examples.ejb.ejbjpa.bo.Floor;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;
import info.ejava.examples.ejb.ejbjpa.bo.Room;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class JPAHotelDAO implements HotelDAO {
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    private <T> TypedQuery<T> withPaging(TypedQuery<T> query, int offset, int limit) {
        if (offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query;
    }
 
    @Override
    public void populate() {
        int roomCount=0;
        for (int f=0; f<3; f++) {
            Floor floor = new Floor(f);
            for (int r=0; r<3; r++) {
                Room room = new Room(floor, f*100 + r);
                floor.withRoom(room);
                if (roomCount++ % 2==0) {
                    Guest guest = new Guest("guest " + roomCount);
                    em.persist(guest);
                    room.setOccupant(guest);
                }
            }
            em.persist(floor);
        }
    }
    
    @Override
    public void clearAll() {
        em.createQuery("update Room r set r.occupant=null").executeUpdate();
        em.createQuery("delete from Room").executeUpdate();
        em.createQuery("delete from Floor").executeUpdate();
        em.createQuery("delete from Guest").executeUpdate();
    }
 
    @Override
    public void addFloor(Floor floor) {
        em.persist(floor);
    }

    @Override
    public Floor getFloor(int level) {
        return em.find(Floor.class,  level);
    }
    
    @Override
    public Floor fetchFloor(int level) {
        List<Floor> floors = em.createNamedQuery("Floor.fetchFloor",
                Floor.class)
                .setParameter("level", level)
                .getResultList();
        return floors.isEmpty() ? null : floors.get(0);
    }

    @Override
    public List<Floor> getFloors(int offset, int limit) {
        return withPaging(em.createNamedQuery("Floor.getFloors", 
                Floor.class),
                offset, limit)
                .getResultList();
    }

    @Override
    public Room getRoom(int number) {
        return em.find(Room.class, number);
    }

    protected TypedQuery<Room> getAvailableRoomsQuery(Integer level, int offset, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Room> qdef = cb.createQuery(Room.class);
        Root<Room> r = qdef.from(Room.class);
        
        Predicate pred = cb.conjunction();
        List<Expression<Boolean>> expr = pred.getExpressions();
        expr.add(cb.isNull(r.get("occupant")));
        if (level!=null) {
            expr.add(cb.equal(r.get("floor").get("level"), level));
        }
        
        qdef.select(r).where(pred).orderBy(cb.asc(r.get("number")));
        return withPaging(em.createQuery(qdef),
                offset, limit);
    }

    @Override
    public List<Room> getAvailableRooms(Integer level, int offset, int limit) {
        return getAvailableRoomsQuery(level, offset, limit)
                .getResultList();
    }
    
    @Override
    public List<Room> getAvailableRoomsForUpdate(Integer level, int offset, int limit) {
        return getAvailableRoomsQuery(level, offset, limit)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setHint("javax.persistence.lock.timeout", 5000)
                .getResultList();
    }
    
    
    @Override
    public Room findRoomByGuest(Guest guest) {
        List<Room> rooms = em.createNamedQuery("Room.findRoomByGuest", 
                Room.class)
                .setParameter("guest", guest)
                .getResultList();
        return rooms.isEmpty() ? null : rooms.get(0);
    }
    
    @Override
    public void addGuest(Guest guest) {
        em.persist(guest);
    }

}
