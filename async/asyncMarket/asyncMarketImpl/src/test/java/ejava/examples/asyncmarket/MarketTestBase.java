package ejava.examples.asyncmarket;

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

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.OrderDAO;
import ejava.examples.asyncmarket.dao.PersonDAO;
import ejava.examples.asyncmarket.jpa.JPAAuctionItemDAO;
import ejava.examples.asyncmarket.jpa.JPAOrderDAO;
import ejava.examples.asyncmarket.jpa.JPAPersonDAO;

/**
 * This class contains common setUp and tearDown logic for the unit tests
 * within this module.
 */
public abstract class MarketTestBase  {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final String PERSISTENCE_UNIT = "asyncMarket-test";
    private static EntityManagerFactory emf;
    protected EntityManager em;
    
    
    protected PersonDAO personDAO;
    protected AuctionItemDAO auctionItemDAO;
    protected OrderDAO orderDAO;

    @BeforeClass
    public static void setUpClass() {
    	emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    @Before
    public void setUp() throws Exception {
    	em=emf.createEntityManager();
        personDAO = new JPAPersonDAO();
        ((JPAPersonDAO)personDAO).setEntityManager(em);
        auctionItemDAO = new JPAAuctionItemDAO();
        ((JPAAuctionItemDAO)auctionItemDAO).setEntityManager(em);
        orderDAO = new JPAOrderDAO();
        ((JPAOrderDAO)orderDAO).setEntityManager(em);
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
	        em.close(); em=null;
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
        List<Bid> bids = 
            em.createQuery("select b from Bid b").getResultList();
        for(Bid b: bids) {
            em.remove(b);
        }
        
        List<AuctionItem> items = 
            em.createQuery("select ai from AuctionItem ai").getResultList();
        for(AuctionItem item: items) {
            em.remove(item);
        }
        
        List<Person> people = 
            em.createQuery("select p from Person p").getResultList();
        for(Person p: people) {
            em.remove(p);
        }        
        
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
