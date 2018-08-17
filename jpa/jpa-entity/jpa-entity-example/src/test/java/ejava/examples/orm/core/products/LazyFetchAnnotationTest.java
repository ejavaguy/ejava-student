package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.orm.core.annotated.Umbrella;

/**
 * This test case provides a demo of using Lazy fetch type on properties 
 * through annotations. Since Lazy Fetch is just a hint and it has been 
 * stated that lazy fetch of Basic data types is of limited value, don't
 * expect much out of this demo. In watching it, all setters are called 
 * before inspecting the object.
 */
public class LazyFetchAnnotationTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(BasicAnnotationTest.class);
    
    /**
     * This test provides a demo of persisting and getting a class that
     * has marked model as LAZY. trace statements have been added to the
     * setters/getters to track activity within object.
     */
    @Test
    public void testLazyFetch() {
        logger.info("testLazyFetch");
        ejava.examples.orm.core.annotated.Umbrella umbrella = new Umbrella(1);
        umbrella.setMake("acme".toCharArray());
        umbrella.setModel("protector");

        //insert a row in the database
        em.persist(umbrella);
        logger.info("created umbrella: {}", umbrella);
        
        em.flush();
        em.clear();        
        Umbrella umbrella2 = em.find(Umbrella.class, 1L);
        assertTrue("didn't get a new object", umbrella != umbrella2);
        
        logger.info("here's model: {}", umbrella2.getModel());
        logger.info("here's make: {}", new String(umbrella2.getMake()));
        logger.info("check setters in logger");        
    }
    
}
