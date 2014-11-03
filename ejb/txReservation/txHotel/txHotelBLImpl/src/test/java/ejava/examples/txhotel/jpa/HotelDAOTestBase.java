package ejava.examples.txhotel.jpa;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;

import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.blimpl.HotelReservationImpl;
import ejava.examples.txhotel.blimpl.HotelReservationSessionImpl;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.dao.ReservationDAO;
import ejava.examples.txhotel.jpa.JPAReservationDAO;

/**
 * This class contains the common setUp and tearDown logic for all
 * DAO tests for the Hotel application.
 */
public abstract class HotelDAOTestBase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "txhotel-test";
    protected HotelReservationist reservationist;
    protected HotelReservationSession reservationSession;
    protected ReservationDAO reservationDAO = null;
    protected EntityManager em;

    @Before
    public void setUp() throws Exception {
        EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = emf.createEntityManager();
        ReservationDAO dao = new JPAReservationDAO();
        ((JPAReservationDAO)dao).setEntityManager(em);
        reservationist = new HotelReservationImpl();
        ((HotelReservationImpl)reservationist).setDao(dao);
        reservationSession = new HotelReservationSessionImpl();
        ((HotelReservationSessionImpl)reservationSession)
            .setReservationist(reservationist);
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        em.getTransaction().begin();
        List<Reservation> reservations = 
            em.createQuery("select r from Reservation r").getResultList();
        for(Reservation r: reservations) {
            r.setPerson(null);
            em.remove(r);
        }
        List<Person> people = 
            em.createQuery("select p from Person p").getResultList();
        for(Person p: people) {
            em.remove(p);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
