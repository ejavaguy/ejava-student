package myorg.queryex;

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


public class QueryBase {
    private static Logger log = LoggerFactory.getLogger(QueryBase.class);
    private static final String PERSISTENCE_UNIT = "queryEx-test";
    protected static EntityManagerFactory emf;
    protected EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        log.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        EntityManager em1 = emf.createEntityManager();
        cleanup(em1);
        populate(em1);
    }
    
    @Before
    public void setUp() throws Exception {
        log.debug("creating entity manager");
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
    	if (em==null || !em.isOpen()) { return; }
        try {
            EntityTransaction tx = em.getTransaction();
            log.debug("tearDown() started, em=" + em);
            if (!tx.isActive()) {
                tx.begin();
                tx.commit();            
            } else if (tx.getRollbackOnly()) {
                tx.rollback();                        	
            } else {
            	tx.commit();
            }
            em.close(); em=null;
            log.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log.error("tearDown failed", ex);
            throw ex;
        }
     }
    
    @AfterClass
    public static void tearDownClass() {
        log.debug("closing entity manager factory");
        if (emf!=null) { emf.close(); emf=null; }
    }
    
    public static void cleanup(EntityManager em) {
    	em.getTransaction().begin();
    	for (Movie movie : em.createQuery("from Movie", Movie.class).getResultList()) {
    		em.remove(movie);
    	}
    	for (Actor actor: em.createQuery("from Actor", Actor.class).getResultList()) {
    		em.remove(actor);
    	}
    	for (Director director: em.createQuery("from Director", Director.class).getResultList()) {
    		em.remove(director);
    	}
    	for (Person person: em.createQuery("from Person", Person.class).getResultList()) {
    		em.remove(person);
    	}
    	em.getTransaction().commit();
    }
    
    public static void populate(EntityManager em) {
    	em.getTransaction().begin();
    	new MovieFactory().setEntityManager(em).populate();
    	em.getTransaction().commit();
    }
}
