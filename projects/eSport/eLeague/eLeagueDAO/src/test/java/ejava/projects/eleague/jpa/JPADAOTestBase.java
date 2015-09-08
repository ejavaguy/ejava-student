package ejava.projects.eleague.jpa;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import ejava.projects.eleague.bo.Venue;

/**
 * This class handles all generic JPA setUp and tearDown actions for 
 * the unit tests 
 */
public class JPADAOTestBase {
    //this code assumes all the JDBC properties were placed in 
    //META-INF/persistence.xml when the file was copied from src to the 
    //target tree
    private static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void setUpClass() throws Exception {
        emf = Persistence.createEntityManagerFactory("eLeagueBO-test");
    }

    @Before
    public void setUp() throws Exception {
        em = emf.createEntityManager();
        
        cleanup();
    }

    @After
    public void tearDown() throws Exception {
        if (em != null && em.getTransaction().isActive()) {
            EntityTransaction tx = em.getTransaction();
            if (tx.getRollbackOnly()) { tx.rollback(); }
            else                      { tx.commit(); }
            em.close(); em=null;
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (emf != null) {
            emf.close(); emf=null;
        }
    }


	
	@SuppressWarnings("unchecked")
	private void cleanup() throws Exception {
		Query query = em.createQuery("select v from Venue v");
		for (Venue venue : (List<Venue>)query.getResultList()) {
			//the Venue entity declared cascade=All to the Address
			//so this should delete the address as well
			em.remove(venue);
		}
		em.getTransaction().begin();
		em.getTransaction().commit();
	}

}
