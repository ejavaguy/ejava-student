package ejava.examples.daoex.jpa;

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

/**
 * This base class performs all common setUp and tearDown functions associated
 * with the child JPA test cases.
 */
public class JPATestBase {
	private static final Logger logger = LoggerFactory.getLogger(JPATestBase.class);
	private static String PU_NAME="jpaDemo";
	
	private static EntityManagerFactory emf;
	protected EntityManager em;

	@BeforeClass
	public static void setUpClass() {
	    logger.debug("setUpClass() getting emf={}", PU_NAME);
	    emf = Persistence.createEntityManagerFactory(PU_NAME);
	    logger.debug("emf.getProperties()={}", emf.getProperties());
	}
	
	@Before
	public void setUp() throws Exception {
	    logger.debug("setUp() getting em");
	    em = emf.createEntityManager();
	    logger.debug("em.getProperties()={}", em.getProperties());
	}

	@After
	public void tearDown() throws Exception {
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            try {
                if (!tx.isActive()) {
                    tx.begin();
                    tx.commit();
                }
                else if (!tx.getRollbackOnly()) {
                    tx.commit();
                }
                else {
                    tx.rollback();
                }
            }
            catch (Exception ex) {
                logger.error("tearDown failed", ex);
                throw ex;
            }
            finally {
                em.close(); 
                em=null;
            }
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
