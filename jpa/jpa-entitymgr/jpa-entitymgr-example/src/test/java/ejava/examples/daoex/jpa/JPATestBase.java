package ejava.examples.daoex.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This base class performs all common setUp and tearDown functions associated
 * with the child JPA test cases.
 */
public class JPATestBase {
	private static final Log log = LogFactory.getLog(JPATestBase.class);
	private static String PU_NAME="jpaDemo";
	
	private static EntityManagerFactory emf;
	protected EntityManager em;

	@BeforeClass
	public static void setUpClass() {
	    log.debug("setUpClass() getting emf=" + PU_NAME);
	    emf = Persistence.createEntityManagerFactory(PU_NAME);
	    log.debug("emf.getProperties()=" + emf.getProperties());
	}
	
	@Before
	public void setUp() throws Exception {
	    log.debug("setUp() getting em");
	    em = emf.createEntityManager();
	    log.debug("em.getProperties()=" + em.getProperties());
	}

	@After
	public void tearDown() throws Exception {
	    try {
	    	if (em != null) {
	    		if (!em.getTransaction().isActive()) {
		            em.getTransaction().begin();
		            em.getTransaction().commit();
	    		}
	    		else if (!em.getTransaction().getRollbackOnly()) {
		            em.getTransaction().commit();
	    		}
	    		else {
		            em.getTransaction().rollback();
	    		}
	    	}
	    }
	    catch (Exception ex) {
	        log.fatal("tearDown failed", ex);
	        throw ex;
	    }
	    finally {
	    	if (em != null) { em.close(); em=null;}
	    }
	}
	
	@AfterClass
	public static void tearDownClass() {
		if (emf != null) {
			emf.close();
			emf=null;
		}
	}

}