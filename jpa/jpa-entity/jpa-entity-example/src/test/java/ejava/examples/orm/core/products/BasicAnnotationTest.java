package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.orm.core.annotated.Bike;

/**
 * This test case provides a demo of using a class that has been mapped
 * to the database with only basic class annotations. All defaults will
 * be determined by the Java Persistence provider.
 * 
 */
public class BasicAnnotationTest extends TestBase {
    private static Log log = LogFactory.getLog(BasicAnnotationTest.class);
    
    /**
     * This test demonstrates the ability to map a simple object to the 
     * database using only the minimal annotations. This only works if
     * we create the database and the names of the Java class are sufficient
     * for use in the database table.
     * 
     * By looking at the Bike class, we would expect to see a Bike table
     * with columns id, make, model, and size. The data types for these 
     * columns are the defaults of the Java Persistence provider. 
     */
    @Test
    public void testDefaultMapping() {
        log.info("testDefaultMapping");
        ejava.examples.orm.core.annotated.Bike bike = new Bike(1);
        bike.setMake("trek");
        bike.setModel("2200");
        bike.setSize(26);
        
        //insert a row in the database
        em.persist(bike);
        log.info("created bike:" + bike);
        
        //find the inserted object
        Bike bike2 = em.find(Bike.class, 1L); 
        //note that the literal value needed to be explicitly expressed as
        //a long so that the Java5 autoboxing didn't incorrectly pass it as
        //an Integer; the data type for the PK is long.
        log.info("found bike:" + bike2);
        assertNotNull(bike2);
        assertEquals(bike2.getSize(), bike2.getSize());
        
        //lets update the size
        bike.setSize(24);
        em.flush();
        //since the persistence context is still active, bike and bike2
        //are actually the same object 
        assertEquals(bike.getSize(), bike2.getSize());
        log.info("updated bike:" + bike2);
        
        //lets delete the object
        em.remove(bike);
        em.flush();
        log.info("removed bike:" + bike);
        
        //lets put a bike back in at end of test so we can see it in database 
        em.persist(bike2);
        log.info("created leftover bike:" + bike2);
    }        
}
