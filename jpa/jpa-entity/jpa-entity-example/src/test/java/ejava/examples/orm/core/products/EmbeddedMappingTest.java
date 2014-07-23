package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.orm.core.mapped.Manufacturer;
import ejava.examples.orm.core.mapped.XRay;

/**
 * This test case provides a demo of the XRay class, which embeds a 
 * Manufacturer into its table mapping.
 */
public class EmbeddedMappingTest extends TestBase {
    private static Log log = LogFactory.getLog(EmbeddedMappingTest.class);
    
    /**
     */
    @Test
    public void testEmbedded() {
        log.info("testEmbedded");
        ejava.examples.orm.core.mapped.XRay xray = new XRay(2);
        xray.setModel("look-at-you");
        xray.setMaker(
                new Manufacturer("hi-tech", "low valley", "410-555-1212"));
        
        //if this works, it should store the single object in 3 tables
        em.persist(xray);
        log.info("created xray:" + xray);
        
        //em.flush();
        //em.clear();
        XRay xray2 = em.find(XRay.class, 2L);
        assertNotNull(xray2);
        log.info("found xray:" + xray2);
        assertEquals(xray.getModel(), xray2.getModel());
        assertEquals(xray.getMaker().getName(), xray2.getMaker().getName());
        assertEquals(xray.getMaker().getAddress(), xray2.getMaker().getAddress());
        assertEquals(xray.getMaker().getPhone(), xray2.getMaker().getPhone());
        
        em.remove(xray2);
        log.info("removed xray:" + xray);
        
        //leave an xray in DB to inspect
        XRay xray3 = new XRay(4);
        xray3.setModel("inside-counts");
        xray3.setMaker(
                new Manufacturer("hi-tech", "low valley", "410-555-1212"));
        em.persist(xray3);
        //em.flush();
        log.info("created leftover xray:" + xray3);
    }        
}
