package ejava.examples.orm.rel;

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

/**
 * This class provides an example of how common text fixtures can be 
 * reused at both the class and object level. Each of the test cases within
 * the suite are inheriting from this class and the @Annotations of this
 * class are causing callbacks within the base to fire. The static/class 
 * variables are reused across the entire suite. The object variables are
 * per testMethod.
 * @author jcstaff
 *
 */
public abstract class DemoBase {
    protected static Log logBase = LogFactory.getLog(DemoBase.class);
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "ormRelations";
    
    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void setUpDownShared() throws Exception {
    	logBase.debug("*** setUpDownShared() ***");
    	emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    @Before
    public void setUpBase() throws Exception {        
        em = emf.createEntityManager();
        precleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDownBase() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        postcleanup();
        em.close();
    }

    @AfterClass
    public static void tearDownBaseClass() throws Exception {
    	logBase.debug("*** tearDownBaseClass() ***");
        if (emf != null) {
        	emf.close();
        	emf = null;
        }
    }
    
    protected void precleanup() throws Exception {}
    protected void postcleanup() throws Exception {}
}