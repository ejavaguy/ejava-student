package ejava.jpa.examples.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

public class QueryBase {
    private static Log log = LogFactory.getLog(QueryBase.class);
    private static final String PERSISTENCE_UNIT = "jpa-query-example-test";
    private List<EntityManager> ems = new ArrayList<EntityManager>();
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
        em = createEm();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
    	for (Iterator<EntityManager> itr=ems.iterator(); itr.hasNext();) {
    		close(itr.next());
    		itr.remove();
    	}
    }
    
    @AfterClass
    public static void tearDownClass() {
        log.debug("closing entity manager factory");
        if (emf!=null) { emf.close(); emf=null; }
    }    

    protected EntityManager createEm() {
    	EntityManager emgr = emf.createEntityManager();
    	ems.add(emgr);
    	return emgr;
    }

    public void close(EntityManager emgr) throws Exception {
    	if (emgr==null || !emgr.isOpen()) { return; }
        try {
            log.debug("tearDown() started, em=" + em);
            if (!emgr.getTransaction().isActive()) {
                emgr.getTransaction().begin();
                emgr.getTransaction().commit();            
            } else if (!emgr.getTransaction().getRollbackOnly()) {
                emgr.getTransaction().commit();                        	
            } else {
            	emgr.getTransaction().rollback();
            }
            emgr.close();
        }
        catch (Exception ex) {
            log.fatal("tearDown failed", ex);
            throw ex;
        }
    }
    
    public static void cleanup(EntityManager em) {
    	em.getTransaction().begin();
    	new SalesFactory().setEntityManager(em).cleanup();
    	em.getTransaction().commit();
    	em.clear();
    }
    
    public static void populate(EntityManager em) {
    	em.getTransaction().begin();
    	new SalesFactory().setEntityManager(em).populate();
    	em.getTransaction().commit();
    	em.clear();
    }
}
