package ejava.examples.txagent.jpa;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;

import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txagent.jpa.JPABookingDAO;

public abstract class AgentDAOTestBase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "txagent-test";
    protected BookingDAO bookingDAO = null;
    protected EntityManager em;

    @Before
    public void setUp() throws Exception {
        EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = emf.createEntityManager();
        bookingDAO = new JPABookingDAO();
        ((JPABookingDAO)bookingDAO).setEntityManager(em);
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
        List<Booking> bookings = 
            em.createQuery("select b from Booking b").getResultList();
        for(Booking b: bookings) {
            em.remove(b);
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
