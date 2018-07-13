package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.orm.core.annotated.Manufacturer;
import ejava.examples.orm.core.annotated.XRay;

/**
 * This test case provides a demo of the XRay class, which embeds a 
 * Manufacturer into its table mapping.
 */
public class EmbeddedAnnotationTest extends TestBase {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedAnnotationTest.class);
    
    /**
     */
    @Test
    public void testEmbedded() {
        log.info("testEmbedded");
        ejava.examples.orm.core.annotated.XRay xray = new XRay(1);
        xray.setModel("look-at-you");
        xray.setMaker(
                new Manufacturer("hi-tech", "low valley", "410-555-1212"));
        
        //if this works, it should store the single object in 3 tables
        em.persist(xray);
        log.info("created xray:" + xray);
        
        em.flush();
        em.clear();
        XRay xray2 = em.find(XRay.class, 1L);
        assertNotNull(xray2);
        log.info("found xray:" + xray2);
        assertEquals(xray.getModel(), xray2.getModel());
        assertEquals(xray.getMaker().getName(), xray2.getMaker().getName());
        assertEquals(xray.getMaker().getAddress(), xray2.getMaker().getAddress());
        assertEquals(xray.getMaker().getPhone(), xray2.getMaker().getPhone());
        
        em.remove(xray2);
        log.info("removed xray:" + xray);
        
        //leave an xray in DB to inspect
        XRay xray3 = new XRay(3);
        xray3.setModel("inside-counts");
        xray3.setMaker(
                new Manufacturer("hi-tech", "low valley", "410-555-1212"));
        em.persist(xray3);
        log.info("created leftover xray:" + xray3);
    }        
}
