package ejava.jpa.example.validation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


public class JPATestBase {
    private static Log log = LogFactory.getLog(JPATestBase.class);
    private static final String PERSISTENCE_UNIT = "jpa-validation-example-test";
    protected static EntityManagerFactory emf;
    protected EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        log.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        log.debug("creating entity manager");
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        try {
            log.debug("tearDown() started, em=" + em);
            EntityTransaction tx = em.getTransaction();
            if (!tx.isActive()) {
                tx.begin();
                tx.commit();            
            } else if (tx.getRollbackOnly()) {
                tx.rollback();                        	
            } else {
            	    tx.commit();
            }
            em.close();
            log.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log.fatal("tearDown failed", ex);
            throw ex;
        }
     }
    
    @AfterClass
    public static void tearDownClass() {
        log.debug("closing entity manager factory");
        if (emf!=null) { emf.close(); }
    }
}
