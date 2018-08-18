package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class TestBase {
    private static final String PERSISTENCE_UNIT = "ormCore";
    private static EntityManagerFactory emf;
    protected EntityManager em = null;

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
        	if (em!=null) {
    	        EntityTransaction tx = em.getTransaction();
    	        if (tx.isActive()) {
    	            if (tx.getRollbackOnly() == true) { tx.rollback(); }
    	            else                              { tx.commit(); }
    	        }
    	        em.close();
    	        em=null;
        	}
    }
    
    @AfterClass
    public static void tearDownBaseClass() {
        	if (emf!=null) {
        		emf.close();
        	}
    }
    
    protected boolean txActive() {
        return em.getTransaction().isActive();
    }
}
