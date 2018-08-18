#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static org.junit.Assert.*;

import static org.junit.Assert.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class AutoTest {
    private static final Logger logger = LoggerFactory.getLogger(Auto.class);
    private static final String PERSISTENCE_UNIT = "${artifactId}-test";
    private static EntityManagerFactory emf;
    private EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        logger.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        logger.debug("creating entity manager");
        em = emf.createEntityManager();
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        logger.debug("tearDown() started, em={}", em);
        if (em!=null) {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                if (tx.getRollbackOnly() == true) { tx.rollback(); }
                else                              { tx.commit(); }
            } else {
                tx.begin();
                tx.commit();
            }
            em.close();
            em=null;
        }
        logger.debug("tearDown() complete, em={}", em);
     }
    
    @AfterClass
    public static void tearDownClass() {
        logger.debug("closing entity manager factory");
        if (emf!=null) { 
            emf.close();
            emf=null;
        }
    }
    
    public void cleanup() {
        em.getTransaction().begin();
        List<Auto> autos = em.createQuery("select a from Auto a", Auto.class)
            .getResultList();
        for (Auto a: autos) {
            em.remove(a);
        }
        em.getTransaction().commit();
        logger.info("removed {} autos", autos.size());
    }

    @Test
    public void testCreate() {
        logger.info("testCreate");
        
        Auto car = new Auto();
        car.setMake("Chrysler");
        car.setModel("Gold Duster");
        car.setColor("Gold");
        car.setMileage(60*1000);
        
        logger.info("creating auto: {}", car);                        
        em.persist(car);        
        
        assertNotNull("car not found", em.find(Auto.class,car.getId()));
    }
}
