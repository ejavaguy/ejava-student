package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assume;
import org.junit.Test;

import ejava.examples.orm.core.mapped.Drill;
import ejava.examples.orm.core.mapped.EggBeater;
import ejava.examples.orm.core.mapped.Fan;
import ejava.examples.orm.core.mapped.Gadget;

/**
 * This test case provides a demo of using automatically generated primary
 * keys setup using class annotations.
 */
public class PKGenMappingTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(BasicAnnotationTest.class);
    
    /**
     * This test provides a demo of using the AUTO GeneratedType. This value
     * is provided by the Java Persistance provider.
     */
    @Test
    public void testAUTOGood() {
        logger.info("testAUTOGood");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.Drill drill = new Drill(0);
        drill.setMake("acme");
        
        //insert a row in the database
        em.persist(drill);
        logger.info("created drill (before flush): {}", drill);
        em.flush(); 
        logger.info("created drill (after flush): {}", drill);
        
        assertFalse(drill.getId() == 0L);        
    }
    
    /**
     * This test provides a demo of the error that can occure when passing an
     * object with the PK already assigned when using GeneratedValues.
     */
    @Test
    public void testAUTOBad() {
        logger.info("testAUTOBad");
        //he's not going to like they non-zero PK value here
        ejava.examples.orm.core.mapped.Drill drill = new Drill(25L);
        drill.setMake("BD");
        
        //insert a row in the database
        boolean exceptionThrown = false;
        try { 
            assertFalse(drill.getId() == 0L);        
            logger.info("trying to create drill with pre-exist pk: {}", drill);
            em.persist(drill);
        }
        catch (PersistenceException ex) {
            logger.info("got expected exception: " + ex);
            exceptionThrown = true;
        }        
        assertTrue(exceptionThrown);
    }        

    @Test
    public void testTABLE() {
        logger.info("testTABLE");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.EggBeater eggbeater = new EggBeater(0);
        eggbeater.setMake("done right");
        
        //insert a row in the database
        em.persist(eggbeater);
        logger.info("created eggbeater (before flush): {}", eggbeater);
        em.flush(); 
        logger.info("created eggbeater (after flush): {}", eggbeater);
        
        assertFalse(eggbeater.getId() == 0L);        
    }

    public void testSEQUENCE() {
        Assume.assumeTrue(Boolean.parseBoolean(System.getProperty("sql.sequences", "true")));
        logger.info("testSEQUENCE");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.Fan fan = new Fan(0);
        fan.setMake("cool runner 2");
        
        //insert a row in the database
        em.persist(fan);
        logger.info("created fan (before flush): {}", fan);
        em.flush(); 
        logger.info("created fan (after flush): {}", fan);
        
        assertFalse(fan.getId() == 0L);                
    }

    @Test
    public void testIDENTITY() {
        logger.info("testIDENTITY");
        ejava.examples.orm.core.mapped.Gadget gadget = new Gadget(0);
        gadget.setMake("gizmo 2");
        
        //insert a row in the database
        em.persist(gadget);
        logger.info("created gadget (before flush): {}", gadget);
        em.flush(); 
        logger.info("created gadget (after flush): {}", gadget);
        
        assertFalse(gadget.getId() == 0L);                
    }
}
