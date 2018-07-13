package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.orm.core.mapped.Watch;

/**
 * This test case provides an example of mapping multiple tables into a 
 * single object. See the javadoc for the Watch class to describe the 
 * mapping details, but essentially 3 tables make-up a single Watch object.
 */
public class MultiTableMappingTest extends TestBase {
    private static final Logger log = LoggerFactory.getLogger(MultiTableMappingTest.class);
    
    /**
     */
    @Test
    public void testMultiTable() {
        log.info("testMultiTable");
        ejava.examples.orm.core.mapped.Watch watch = new Watch(2);
        watch.setMake("ontime");
        watch.setModel("round-and-round");
        watch.setOwner("john doe");
        watch.setCardnum("123-45-6789");
        watch.setManufacturer("getter done");
        watch.setAddress("12noon lane");
        watch.setPhone("410-555-1212");
        
        //if this works, it should store the single object in 3 tables
        em.persist(watch);
        log.info("created watch:" + watch);
        
        em.flush();
        em.clear();
        Watch watch2 = em.find(Watch.class, 2L);
        assertNotNull(watch2);
        log.info("found watch:" + watch2);
        assertEquals(watch.getMake(), watch2.getMake());
        assertEquals(watch.getModel(), watch2.getModel());
        assertEquals(watch.getOwner(), watch2.getOwner());
        assertEquals(watch.getCardnum(), watch2.getCardnum());
        assertEquals(watch.getManufacturer(), watch2.getManufacturer());
        assertEquals(watch.getAddress(), watch2.getAddress());
        assertEquals(watch.getPhone(), watch2.getPhone());
        
        em.remove(watch2);
        log.info("removed watch:" + watch);
        
        //leave a watch in DB to inspect
        Watch watch3 = new Watch(4);
        watch3.setMake("ontime3");
        watch3.setModel("round-and-round3");
        watch3.setOwner("john doe3");
        watch3.setCardnum("123-45-67893");
        watch3.setManufacturer("getter done3");
        watch3.setAddress("12noon lane3");
        watch3.setPhone("410-555-12123");       
        em.persist(watch3);
        log.info("created leftover watch:" + watch3);
    }        
}
