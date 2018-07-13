package ejava.examples.orm.inheritance;

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

public abstract class DemoBase {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final String PERSISTENCE_UNIT = "ormInheritance";
    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void setUpBaseClass() {
    	emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUpBase() throws Exception {        
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @After
    public void tearDownBase() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
    }
    
    @AfterClass
    public static void tearDownBaseClass() {
    	if (emf != null) {
    		emf.close();
    		emf = null;
    	}
    }
}
