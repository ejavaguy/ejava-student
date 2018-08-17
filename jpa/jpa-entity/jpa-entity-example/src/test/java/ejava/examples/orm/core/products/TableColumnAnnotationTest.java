package ejava.examples.orm.core.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import javax.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.orm.core.annotated.Car;

/**
 * This test case provides a demo of using a class that has been mapped
 * to the database with with specific table and column annotations. 
 */
public class TableColumnAnnotationTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TableColumnAnnotationTest.class);
    
    @Before
    public void cleanup() {
    	for (Car car: em.createQuery("select c from Car c", Car.class).getResultList()) {
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
        logger.info("testTableColumnMapping");

        try {           
            ejava.examples.orm.core.annotated.Car car = new Car(1);
            car.setMake("chevy");
            car.setModel("tahoe");
            car.setYear(2002);
            car.setCost(new BigDecimal(10000.00));
            
            //insert a row in the database
            em.persist(car);
            logger.info("created car: {}", car);
            
            //find the inserted object
            Car car2 = em.find(Car.class, 1L); 
    
            logger.info("found car: {}", car2);
            assertNotNull(car2);
            assertEquals(car.getYear(), car2.getYear());
            
            //lets update the year
            car.setYear(2000);
            em.flush();
            //since the persistence context is still active, cars
            //are actually the same object 
            assertEquals(car.getYear(), car2.getYear());
            logger.info("updated car: {}", car2);
            
            //lets delete the object
            em.remove(car);
            em.flush();
            logger.info("removed car: {}", car);
            
            //lets put a car back in at end of test so we can see it in database 
            em.persist(car2);
            logger.info("created leftover car: {}", car2);
            
        } catch (PersistenceException ex) {
            StringBuilder text = new StringBuilder(ex.getMessage());
            Throwable cause = ex.getCause();
            while (cause != null) {
                text.append("\nCaused By:" + cause);
                cause = cause.getCause();
            }
            logger.error("error in testTableColumnMapping:" + text, ex);
            fail("error in testTableColumnMapping:" + text);
        }
    }
    
    /**
     * Demonstrates the use of precision and scale
     */
    @Test
    public void testPrecision() {
        ejava.examples.orm.core.annotated.Car car = new Car(1);
        car.setMake("chevy");
        car.setModel("tahoe");
        car.setYear(2002);
        //precision defined in ORM as precision=7, scale=2 
        car.setCost(new BigDecimal("12345.66"));
        
        //persist with current values  
        	em.persist(car);
        	em.flush();
        	em.clear();
    	
        	//get a fresh copy from the DB
        	Car car2 = em.find(Car.class, car.getId());
        	logger.info("car.cost=" + car.getCost());
        	logger.info("car2.cost=" + car2.getCost());
        	assertEquals("unexpectected value", car.getCost(), car2.getCost());
        	
        	
        	//update beyond the scale values -- too many digits to right of decimal
        car2.setCost(new BigDecimal("1234.666"));
        	em.flush();
        	em.clear();
        	Car car3 = em.find(Car.class, car.getId());
        	logger.info("car2.cost=" + car2.getCost());
        	logger.info("car3.cost=" + car3.getCost());
        	assertNotEquals("unexpected scale", car2.getCost(), car3.getCost());
        	
        	//update beyond the precision values -- too many digits overall
        	car2 = car3;
        car2.setCost(new BigDecimal("123456.66"));
        try {
	    	em.flush();
	    	fail("database accepted too many digits");
	    	em.clear();
	    	car3 = em.find(Car.class, car.getId());
	    	logger.info("car2.cost=" + car2.getCost());
	    	logger.info("car3.cost=" + car3.getCost());
	    	assertFalse("unexpected precision", car2.getCost().equals(car3.getCost()));
        } catch (PersistenceException ex) {
        	logger.info("caught expected exception:" + ex);
        }
    }
}
