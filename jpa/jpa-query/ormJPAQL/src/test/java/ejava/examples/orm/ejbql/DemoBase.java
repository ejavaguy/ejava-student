package ejava.examples.orm.ejbql;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;

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

import ejava.examples.orm.ejbql.annotated.Clerk;
import ejava.examples.orm.ejbql.annotated.Customer;
import ejava.examples.orm.ejbql.annotated.Sale;
import ejava.examples.orm.ejbql.annotated.Store;

public abstract class DemoBase {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final String PERSISTENCE_UNIT = "ormEJBQL";
    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void setUpClass() {
    	emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);    	
    }
    
    @Before
    public void setUp() throws Exception {        
        em = emf.createEntityManager();
        cleanup();
        populate();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
    }
    
    @AfterClass
    public static void tearDownClass() {
    	if (emf != null) {
    		emf.close();
    		emf = null;
    	}
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        em.getTransaction().begin();
        
        Collection<Store> stores = 
            em.createQuery("select s from Store s").getResultList();
        for(Store s : stores) {
            em.remove(s);
        }
        em.flush();
        
        em.createQuery("delete from Customer c").executeUpdate();
        em.createQuery("delete from Clerk c").executeUpdate();
        
        
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        em.getTransaction().begin();
        
        Store store = new Store();
        store.setName("Big Al's");
        em.persist(store);
        em.flush();

        Calendar hireDate = Calendar.getInstance();

        Clerk manny = new Clerk();
        manny.setFirstName("Manny");
        manny.setLastName("Pep");
        hireDate.set(1970, 01, 01, 0, 0);
        manny.setHireDate(hireDate.getTime());

        Clerk moe = new Clerk();
        moe.setFirstName("Moe");
        moe.setLastName("Pep");
        hireDate.set(1970, 01, 01, 0, 0);
        moe.setHireDate(hireDate.getTime());

        Clerk jack = new Clerk();
        jack.setFirstName("Jack");
        jack.setLastName("Pep");
        hireDate.set(1973, 01, 01, 0, 0);
        jack.setHireDate(hireDate.getTime());

        em.persist(manny);
        em.persist(moe);
        em.persist(jack);
        em.flush();
        
        Customer cat = new Customer();
        cat.setFirstName("cat");
        cat.setLastName("inhat");
        
        Customer one = new Customer();
        one.setFirstName("thing");
        one.setLastName("one");
        
        Customer two = new Customer();
        two.setFirstName("thing");
        two.setLastName("two");
        
        em.persist(cat);
        em.persist(one);
        em.persist(two);
        em.flush();
        
        Calendar saleDate = Calendar.getInstance();

        Sale sale1 = new Sale();
        sale1.setAmount(new BigDecimal(100.00));
        sale1.setBuyerId(0);
        saleDate.set(1998,03,10,0,0);
        sale1.setDate(saleDate.getTime());
        sale1.getClerks().add(manny);
        manny.getSales().add(sale1);
        sale1.setBuyerId(cat.getId());
        sale1.setStore(store);
        store.getSales().add(sale1);
        
        
        Sale sale2 = new Sale();
        sale2.setAmount(new BigDecimal(150.00));
        sale2.setBuyerId(0);
        saleDate.set(1999,05,11,0,0);
        sale2.setDate(saleDate.getTime());
        sale2.getClerks().add(manny);
        manny.getSales().add(sale2);
        sale2.getClerks().add(moe);
        moe.getSales().add(sale2);
        sale2.setBuyerId(one.getId());
        sale2.setStore(store);
        store.getSales().add(sale2);
        
        em.persist(sale1);
        em.persist(sale2);
        
        em.getTransaction().commit();
    }
}
