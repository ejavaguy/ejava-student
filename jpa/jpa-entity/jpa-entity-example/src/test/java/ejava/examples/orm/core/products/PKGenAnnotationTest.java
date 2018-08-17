package ejava.examples.orm.core.products;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.orm.core.annotated.Drill;
import ejava.examples.orm.core.annotated.EggBeater;
import ejava.examples.orm.core.annotated.Fan;
import ejava.examples.orm.core.annotated.Gadget;

/**
 * This test case provides a demo of using automatically generated primary
 * keys setup using class annotations.
 */
public class PKGenAnnotationTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(BasicAnnotationTest.class);
    
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
        logger.info("testAUTOGood");
        //since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.annotated.Drill drill = new Drill(0);
        drill.setMake("acme");
        
        //insert a row in the database
        logger.info("just before persist(tx={}): {}", txActive(), drill);
        em.persist(drill);
        logger.info("created drill (after persist and before flush, tx={}): {}", txActive(), drill);
        em.flush(); 
        logger.info("created drill (after flush, tx={}): {}", txActive(), drill);
        
        assertNotEquals(0, drill.getId());        
    }
    
    /**
     * This test provides a demo of the error that can occure when passing an
     * object with the PK already assigned when using GeneratedValues.
     */
    @Test
    public void testAUTOBad() {
        logger.info("testAUTOBad");
        //provider will not like the non-zero PK value here
        //because we told it to generate the PK
        ejava.examples.orm.core.annotated.Drill drill = new Drill(25L);
        drill.setMake("BD");
        
        //insert a row in the database
        boolean exceptionThrown = false;
        try { 
            assertNotEquals(0, drill.getId());        
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
        logger.debug("table id before(tx={})={}", txActive(), getTableId());
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.annotated.EggBeater eggbeater = new EggBeater(0);
        eggbeater.setMake("done right 1");
        
        //insert a row in the database
        logger.info("persisting eggbeater (tx={}): {}", txActive(), eggbeater);
        em.persist(eggbeater);
        logger.info("created eggbeater (before flush; tx={}): {}", txActive(), eggbeater);
        em.flush(); 
        logger.info("created eggbeater (after flush; tx={}): {}", txActive(), eggbeater);
        
        assertNotEquals(0, eggbeater.getId());   
        logger.debug("table id after(tx={})={}", txActive(), getTableId());
        
        int counter=2;
        for (int i=0; i<20; i++) {
            	EggBeater eb = new EggBeater();
            	eb.setMake("done right " + counter);
            	em.persist(eb);
            logger.info("created ehhbeater(tx={}): {}", txActive(), eb);
            logger.debug("table id after[" + counter++ + "](tx={})={}", txActive(), getTableId());
            if (i==0) {
                eggbeater=eb;
            }
        }
        logger.info("committing (tx={}): {}", txActive(), eggbeater);
        em.getTransaction().commit();
        logger.info("tx committed (tx={}): {}", txActive(), eggbeater);
        
        logger.info("tx(tx={})", txActive());
        for (int i=0; i<20; i++) {
            EggBeater eb = new EggBeater();
            eb.setMake("done right " + counter);
            em.persist(eb);
            logger.info("created ehhbeater(tx={}): {}", txActive(), eb);
            logger.debug("table id after[" + counter++ + "](tx={})={}", txActive(), getTableId());
            if (i==0) {
                eggbeater=eb;
            }
        }
        
        logger.info("starting tx(tx={}): {}", txActive(), eggbeater);
        em.getTransaction().begin();
        logger.info("tx started, flushing (tx={}): {}", txActive(), eggbeater);
        em.flush();
        logger.info("cache flushed (tx={}): {}", txActive(), eggbeater);
        em.getTransaction().commit();
        logger.info("tx committed (tx={}): {}", txActive(), eggbeater);
    }
    
    protected Integer getTableId() {
        List<?> results = em.createNativeQuery(
        		"select UID_VAL from ORMCORE_EB_UID " +
		        "where UID_ID='ORMCORE_EGGBEATER'")
		        .getResultList();
        return results.size() == 0 ? null : ((Number)results.get(0)).intValue();
    }


    @Test
    public void testSEQUENCE() {
        logger.info("testSEQUENCE");
        Assume.assumeTrue(Boolean.parseBoolean(System.getProperty("sql.sequences", "true")));
        try {
            ejava.examples.orm.core.annotated.Fan fan = new Fan(0);
            fan.setMake("cool runner 1");
            
            //insert a row in the database
            logger.info("persisting fan(tx={}): {}", txActive(), fan);
            em.persist(fan);
            logger.info("created fan (before flush, tx={}): {}", txActive(), fan);
            em.flush(); 
            logger.info("created fan (after flush; tx={}): {}", txActive(), fan);            
            assertNotEquals(0, fan.getId());
            
            int counter=2;
            for (int i=0; i<20; i++) {               
                Fan f = new Fan();
            	    f.setMake("cool runner " + counter++);
            	    em.persist(f);
                logger.info("created fan(tx={}): {}", txActive(), f);
                if (i==0) {
                    fan=f;
                }
            }
            logger.info("committing (tx={}): {}", txActive(), fan);
            em.getTransaction().commit();
            logger.info("tx committed (tx={}): {}", txActive(), fan);
            
            logger.info("tx(tx={})", txActive());
            for (int i=0; i<20; i++) {
                Fan f = new Fan();
                f.setMake("cool runner " + counter++);
                em.persist(f);
                logger.info("created fan(tx={}): {}", txActive(), f);
                if (i==0) {
                    fan=f;
                }
            }
            
            logger.info("starting tx(tx={}): {}", txActive(), fan);
            em.getTransaction().begin();
            logger.info("tx started, flushing (tx={}): {}", txActive(), fan);
            em.flush();
            logger.info("cache flushed (tx={}): {}", txActive(), fan);
            em.getTransaction().commit();
            logger.info("tx committed (tx={}): {}", txActive(), fan);
            
        } catch (PersistenceException ex) {
            String text = getText(ex);
            logger.error("error in testSEQUENCE:" + text, ex);
            fail("error in testSEQUENCE:" + text);
        }
    }

    @Test
    public void testIDENTITY() {
        logger.info("testIDENTITY");
        try {
            ejava.examples.orm.core.annotated.Gadget gadget = new Gadget(0);
            gadget.setMake("gizmo 1");
            
            //insert a row in the database
            //start with a tx already active
            logger.info("gadget (before persist; tx={}): {}", txActive(), gadget);
            em.persist(gadget);
            logger.info("created gadget (after persist, before flush; tx={}): {}", txActive(), gadget);
            em.flush(); 
            logger.info("created gadget (after flush; tx={}): {}", txActive(), gadget);            
            assertNotEquals(0, gadget.getId());     

            int counter=2;
            for (int i=0; i<3; i++) {
                Gadget g = new Gadget();
                g.setMake("gizmo " + counter++);
                em.persist(g);
                logger.info("created gadget(tx={}): {}", txActive(), g);
            }
            
            em.getTransaction().rollback();
            logger.info("rolled back tx(tx={})", txActive());
            for (int i=0; i<3; i++) {
                Gadget g = new Gadget();
                g.setMake("gizmo " + counter++);
                em.persist(g);
                logger.info("created gadget(tx={}): {}", txActive(), g);
                if (i==0) {
                    gadget=g;
                }
            }
            
            logger.info("starting tx(tx={}): {}", txActive(), gadget);
            em.getTransaction().begin();
            logger.info("tx started, flushing (tx={}): {}", txActive(), gadget);
            em.flush();
            logger.info("cache flushed (tx={}): {}", txActive(), gadget);
            em.getTransaction().commit();
            logger.info("tx committed (tx={}): {}", txActive(), gadget);            
        } catch (PersistenceException ex) {
            String text = getText(ex);
            logger.error("error in testIDENTITY:" + text, ex);
            fail("error in testIDENTITY:" + text);
        }
        
    }
    
}
