package ejava.jpa.examples.tuning.stuff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

import ejava.jpa.examples.tuning.MovieFactory;


@Ignore
public class QueryBase {
    private static Logger log = LoggerFactory.getLogger(QueryBase.class);
    private static final String PERSISTENCE_UNIT = "queryEx-test";
    protected static EntityManagerFactory emf;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        EntityManager em1 = emf.createEntityManager();
        cleanup(em1);
        populate(em1);
    }
    
    @AfterClass
    public static void tearDownClass() {
        log.trace("closing entity manager factory");
        if (emf!=null) { emf.close();  emf=null; }
    }
    
    public static void cleanup(EntityManager em) {
    	new MovieFactory().setEntityManager(em).cleanup();
    }
    
    public static void populate(EntityManager em) {
    	new MovieFactory().setEntityManager(em).populate();
    }
}
