package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assume;
import org.junit.Test;

import ejava.examples.orm.core.annotated.Drill;
import ejava.examples.orm.core.annotated.EggBeater;
import ejava.examples.orm.core.annotated.Fan;
import ejava.examples.orm.core.annotated.Gadget;

/**
 * This test case provides a demo of using automatically generated primary
 * keys setup using class annotations.
 */
public class PKGenAnnotationTest extends TestBase {
    private static Log log = LogFactory.getLog(BasicAnnotationTest.class);
    
    static String getText(Throwable ex) {
        StringBuilder text = new StringBuilder(ex.getMessage());
        Throwable cause = ex.getCause();
        while (cause != null) {
            text.append("\nCaused By:" + cause);
            cause = cause.getCause();
        }
        return text.toString();
    }
    
    /**
     * This test provides a demo of using the AUTO GeneratedType. This value
     * is provided by the Java Persistance provider.
     */
    @Test
    public void testAUTOGood() {
        log.info("testAUTOGood");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.annotated.Drill drill = new Drill(0);
        drill.setMake("acme");
        
        //insert a row in the database
        em.persist(drill);
        log.info("created drill (before flush):" + drill);
        em.flush(); 
        log.info("created drill (after flush):" + drill);
        
        assertFalse(drill.getId() == 0L);        
    }
    
    /**
     * This test provides a demo of the error that can occure when passing an
     * object with the PK already assigned when using GeneratedValues.
     */
    @Test
    public void testAUTOBad() {
        log.info("testAUTOBad");
        //he's not going to like they non-zero PK value here
        ejava.examples.orm.core.annotated.Drill drill = new Drill(25L);
        drill.setMake("BD");
        
        //insert a row in the database
        boolean exceptionThrown = false;
        try { 
            assertFalse(drill.getId() == 0L);        
            log.info("trying to create drill with pre-exist pk:" + drill);
            em.persist(drill);
        }
        catch (PersistenceException ex) {
            log.info("got expected exception: " + ex);
            exceptionThrown = true;
        }        
        assertTrue(exceptionThrown);
    }        

    @Test
    public void testTABLE() {
        log.info("testTABLE");
        log.debug("table id before=" + getTableId());
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.annotated.EggBeater eggbeater = new EggBeater(0);
        eggbeater.setMake("done right");
        
        //insert a row in the database
        em.persist(eggbeater);
        log.info("created eggbeater (before flush):" + eggbeater);
        em.flush(); 
        log.info("created eggbeater (after flush):" + eggbeater);
        
        assertFalse(eggbeater.getId() == 0L);   
        log.debug("table id after=" + getTableId());
        for (int i=2; i<20; i++) {
        	EggBeater eb = new EggBeater();
        	em.persist(eb);
            log.info("created ehhbeater:" + eb);
            log.debug("table id after[" + i + "]=" + getTableId());        	
        }
    }
    
    protected Integer getTableId() {
        List<?> results = em.createNativeQuery(
        		"select UID_VAL from ORMCORE_EB_UID " +
		        "where UID_ID='ORMCORE_EGGBEATER'")
		        .getResultList();
        return results.size() == 0 ? null : (Integer)results.get(0);
    }


    @Test
    public void testSEQUENCE() {
        log.info("testSEQUENCE");
        Assume.assumeTrue(Boolean.parseBoolean(System.getProperty("sql.sequences", "true")));
        try {
            //note that since PKs are generated, we must pass in an object that
            //has not yet been assigned a PK value.
            ejava.examples.orm.core.annotated.Fan fan = new Fan(0);
            fan.setMake("cool runner 1");
            
            //insert a row in the database
            em.persist(fan);
            log.info("created fan (before flush):" + fan);
            em.flush(); 
            log.info("created fan (after flush):" + fan);
            
            assertFalse(fan.getId() == 0L);
            
            for (int i=2; i<20; i++) {
            	Fan f = new Fan();
            	f.setMake("cool runner " + i);
            	em.persist(f);
                log.info("created fan:" + f);
            }
        } catch (PersistenceException ex) {
            String text = getText(ex);
            log.error("error in testSEQUENCE:" + text, ex);
            fail("error in testSEQUENCE:" + text);
        }
    }

    @Test
    public void testIDENTITY() {
        log.info("testIDENTITY");
        try {
            ejava.examples.orm.core.annotated.Gadget gadget = new Gadget(0);
            gadget.setMake("gizmo 1");
            
            //insert a row in the database
            em.persist(gadget);
            log.info("created gadget (before flush):" + gadget);
            em.flush(); 
            log.info("created gadget (after flush):" + gadget);
            
            assertFalse(gadget.getId() == 0L);                
            
            for (int i=2; i<20; i++) {
            	Gadget g = new Gadget();
            	g.setMake("gizmo " + i);
            	em.persist(g);
                log.info("created gadget:" + g);
            }
        } catch (PersistenceException ex) {
            String text = getText(ex);
            log.error("error in testIDENTITY:" + text, ex);
            fail("error in testIDENTITY:" + text);
        }
    }
    
}
