package ejava.examples.txhotel.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.dao.ReservationDAO;
import ejava.examples.txhotel.jpa.JPAReservationDAO;

import junit.framework.TestCase;

public abstract class DAOTestBase extends TestCase {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final String PERSISTENCE_UNIT = "txhotel-test";
    protected ReservationDAO reservationDAO = null;
    protected EntityManager em;

    protected void setUp() throws Exception {
        EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);                                
        em = emf.createEntityManager();
        reservationDAO = new JPAReservationDAO();
        ((JPAReservationDAO)reservationDAO).setEntityManager(em);
        cleanup();
        em.getTransaction().begin();
    }

    protected void tearDown() throws Exception {
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
