package ejava.examples.ejbsessionbank;

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

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.jpa.JPAAccountDAO;

public class DemoBase {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final String PERSISTENCE_UNIT = "ejbsessionBank-test";
    protected AccountDAO acctDAO = null;
    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void setUpClass() {
    	emf=Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        em = emf.createEntityManager();
        acctDAO = new JPAAccountDAO();
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
    	if (em != null) {
	        EntityTransaction tx = em.getTransaction();
	        if (tx.isActive()) {
	            if (tx.getRollbackOnly() == true) { tx.rollback(); }
	            else                              { tx.commit(); }
	        }
	        em.close(); em = null;
    	}
    }
    
    @AfterClass
    public static void tearDownClass() {
    	if (emf != null) {
    		emf.close(); emf=null;
    	}
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        em.getTransaction().begin();
        
        List<Owner> owners = 
            em.createQuery("select o from Owner o").getResultList();
        log.debug("found " + owners.size() + " owners");
        for (Owner o : owners) {
            em.remove(o);
            em.flush();
        }
        
        //delete orphan accounts
        List<Account> accounts = 
            em.createQuery("select a from Account a " +
            		"where NOT EXISTS " +
            		"(select o from Owner o " +
            		"where a MEMBER OF o.accounts)").getResultList();
        log.debug("found " + accounts.size() + " accounts");
        for(Account a: accounts) {
            em.remove(a);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
