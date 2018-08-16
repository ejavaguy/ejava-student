package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

import ejava.examples.orm.core.mapped.Car;

/**
 * This test case provides a demo of using a class that has been mapped
 * to the database with with specific table and column elements in orm.xml. 
 */
public class TableColumnMappingTest extends TestBase {
    private static final Logger log = LoggerFactory.getLogger(TableColumnMappingTest.class);
    
    @Before
    public void cleanup() {
    	for (Car car: em.createQuery("select c from MappedCar c", Car.class).getResultList()) {
    		em.remove(car);
    	}
    	em.getTransaction().commit();
    	em.getTransaction().begin();
    }
    
    /**
     * This test demonstrates the ability to map a simple object to the 
     * database using custom table and column annotations.
     * 
     * By looking at the Car class, we would expect to see a ORBCORE_CAR table
     * with columns CAR_ID, CAR_MAKE, CAR_MODEL, CAR_YEAR, and CAR_COST. 
     */
    @Test
    public void testTableColumnMapping() {
        log.info("testTableColumnMapping");
        
        try {
            ejava.examples.orm.core.mapped.Car car = new Car(2);
            car.setMake("chevy");
            car.setModel("tahoe");
            car.setYear(2002);
            car.setCost(new BigDecimal("10000.00"));
            
            //insert a row in the database
            em.persist(car);
            log.info("created car:" + car);
            
            //find the inserted object
            Car car2 = em.find(Car.class, 2L); 
    
            log.info("found car:" + car2);
            assertNotNull(car2);
            assertEquals(car.getYear(), car2.getYear());
            
            //lets update the year
            car.setYear(2000);
            em.flush();
            //since the persistence context is still active, cars
            //are actually the same object 
            assertEquals(car.getYear(), car2.getYear());
            log.info("updated car:" + car2);
            
            //lets delete the object
            em.remove(car);
            em.flush();
            log.info("removed car:" + car);
            
            //lets put a car back in at end of test so we can see it in database 
            em.persist(car2);
            log.info("created leftover car:" + car2);
        } catch (PersistenceException ex) {
            StringBuilder text = new StringBuilder(ex.getMessage());
            Throwable cause = ex.getCause();
            while (cause != null) {
                text.append("\nCaused By:" + cause);
                cause = cause.getCause();
            }
            log.error("error in testTableColumnMapping:" + text, ex);
            fail("error in testTableColumnMapping:" + text);
        }

    }        
}
