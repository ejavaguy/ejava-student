package myorg.entitymgrex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class EntityMgrTest {
    private static Log log = LogFactory.getLog(EntityMgrTest.class);
    private static final String PERSISTENCE_UNIT = "entityMgrEx";
    private static EntityManagerFactory emf;
    private EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        log.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    @Before
    public void setUp() throws Exception {
        log.debug("creating entity manager");
        em = emf.createEntityManager();
        cleanup();
    }

    @After
    public void tearDown() throws Exception {
        try {
            log.debug("tearDown() started, em=" + em);
            em.getTransaction().begin();
            em.flush();            
            logAutos();            
            em.getTransaction().commit();            
            em.close();
            log.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log.fatal("tearDown failed", ex);
            throw ex;
        }
     }

    public void cleanup() {
        em.getTransaction().begin();
        Query query = em.createNativeQuery("delete from EM_AUTO");
        int rows = query.executeUpdate();
        em.getTransaction().commit();
        log.info("removed " + rows + " rows");
    }
    public void logAutos() {
        Query query = em.createQuery("select a from Auto as a");
        for (Object o: query.getResultList()) {
            log.info("EM_AUTO:" + o);
        }        
    }

    @AfterClass
    public static void tearDownClass() {
        log.debug("closing entity manager factory");
        emf.close();
    }

    @Test
    public void testTemplate() {
        log.info("testTemplate");
    }

    @Test
    public void testCreate() {
        log.info("testCreate");
        
        Auto car = new Auto();
        car.setMake("Chrysler");
        car.setModel("Gold Duster");
        car.setColor("Gold");
        car.setMileage(60*1000);
        
        log.info("creating auto:" + car);                        
        em.persist(car);        
    }

    @Test
    public void testMultiCreate() {
        log.info("testMultiCreate");
        for(int i=0; i<5; i++) {
            Auto car = new Auto();
            car.setMake("Plymouth " + i);
            car.setModel("Grand Prix");
            car.setColor("Green");
            car.setMileage(80*1000);            
            log.info("creating auto:" + car);                        
            em.persist(car);        
        }
    }

    @Test
    public void testFind() {
        log.info("testFind");
        
        Auto car = new Auto();
        car.setMake("Ford");
        car.setModel("Bronco II");
        car.setColor("Red");
        car.setMileage(0*1000);
        log.info("creating auto:" + car);                        
        em.persist(car);
        
        //we need to associate the em with a transaction to get a 
        //primary key generated and assigned to the auto
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        Auto car2 = em.find(Auto.class, car.getId());
        assertNotNull("car not found:" + car.getId(), car2);
        log.info("found car:" + car2);
    }

    @Test
    public void testGetReference() {
        log.info("testGetReference");
        
        Auto car = new Auto();
        car.setMake("Ford");
        car.setModel("Escort");
        car.setColor("Red");
        car.setMileage(0*1000);
        log.info("creating auto:" + car);                        
        em.persist(car);
        
        //we need to associate the em with a transaction to get a 
        //primary key generated and assigned to the auto
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        Auto car2 = em.getReference(Auto.class, car.getId());
        assertNotNull("car not found:" + car.getId(), car2);
        log.info("found car:" + car2);        
    }

    @Test
    public void testUpdate() {
        log.info("testUpdate");
        
        Auto car = new Auto();
        car.setMake("Pontiac");
        car.setModel("Gran Am");
        car.setColor("Red");
        car.setMileage(0*1000);
        log.info("creating auto:" + car);                        
        em.persist(car);
        
        //we need to associate the em with a transaction to get a 
        //primary key generated and assigned to the auto
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        for(int mileage=car.getMileage(); mileage<(100*1000); mileage+=20000) {
            //here's where the update is done
            car.setMileage(mileage);
            
            //commit the update to the database for query 
            em.getTransaction().begin();
            em.getTransaction().commit();
            
            //inspect database for value
            int value = getMileage(car.getId());
            assertTrue("unexpected mileage:" + value, value == mileage);
            log.info("found mileage:" + value);        
        }
        
    }

    private int getMileage(long id) {
        Query query = 
            em.createQuery("select a.mileage from Auto as a where a.id=:pk");
        query.setParameter("pk", id);
        return (Integer)query.getSingleResult();        
    }

    @Test
    public void testMerge() throws Exception {
        log.info("testMerge");
        
        Auto car = new Auto();
        car.setMake("Chrystler");
        car.setModel("Concord");
        car.setColor("Red");
        car.setMileage(0*1000);
        log.info("creating auto:" + car);                        
        car = em.merge(car); //using merge to persist new
        
        //we need to associate the em with a transaction to get a 
        //primary key generated and assigned to the auto
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        for(int mileage=(10*1000); mileage<(100*1000); mileage+=20000) {
            //simulate sending to remote system for update
            Auto car2 = updateMileage(car, mileage);
            
            //verify the object is not being managed by the EM
            assertFalse("object was managed", em.contains(car2));
            assertTrue("object wasn't managed", em.contains(car));
            assertTrue("mileage was same", 
                    car.getMileage() != car2.getMileage());
            
            //commit the update to the database for query 
            em.merge(car2);
            assertTrue("car1 not merged:" + car.getMileage(), 
                    car.getMileage() == mileage);
            em.getTransaction().begin();
            em.getTransaction().commit();
            
            //inspect database for value
            int value = getMileage(car.getId());
            assertTrue("unexpected mileage:" + value, value == mileage);
            log.info("found mileage:" + value);        
        }        
    }
    
    private Auto updateMileage(Auto car, int mileage) throws Exception {
        //simulate sending the object to a remote system
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(car);
        oos.close();
        
        //simulate receiving an update to the object from remote system
        ByteArrayInputStream bis = 
            new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        Auto car2 = (Auto)ois.readObject();
        ois.close();
        
        //here's what they would have changed in remote process 
        car2.setMileage(mileage);
        
        return car2;
    }

    @Test
    public void testRemove() {
        log.info("testRemove");
        
        Auto car = new Auto();
        car.setMake("Jeep");
        car.setModel("Cherokee");
        car.setColor("Green");
        car.setMileage(30*1000);
        log.info("creating auto:" + car);                        
        em.persist(car);

        //we need to associate the em with a transaction to get a 
        //primary key generated and assigned to the auto
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        Auto car2 = em.find(Auto.class, car.getId());
        assertNotNull("car not found:" + car.getId(), car2);
        log.info("found car:" + car2);
        
        //now remove the car
        log.info("removing car:" + car);
        em.remove(car);
        //we need to associate the em with a transaction to  
        //physically remove from database
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        Auto car3 = em.find(Auto.class, car.getId());
        assertNull("car found", car3);
    }    
}
