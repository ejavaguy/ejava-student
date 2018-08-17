package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.orm.core.annotated.MakeModelPK;
import ejava.examples.orm.core.annotated.Mower;
import ejava.examples.orm.core.annotated.Napsack;
import ejava.examples.orm.core.annotated.NapsackPK;
import ejava.examples.orm.core.annotated.Pen;
import ejava.examples.orm.core.MowerPK;

/**
 * This test case provides a demo of using customer primary key classes
 * specified by annotations.
 */
public class PKClassAnnotationTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(BasicAnnotationTest.class);
    
    /**
     * This test provides a demo of using the AUTO GeneratedType. This value
     * is provided by the Java Persistence provider.
     */
    @Test
    public void testIdClass() {
        logger.info("testIdClass");
        ejava.examples.orm.core.annotated.Mower mower =  new Mower("acme", "power devil2");
        mower.setSize(21);
        
        //insert a row in the database
        logger.info("persisting mower(tx={}): {}", txActive(), mower);
        em.persist(mower);
        logger.info("created mower: {}", mower);
        em.flush();
        logger.info("flushed");
        
        //locate instance by ID, while instance still managed        
        Mower mower2 = em.find(Mower.class, new MowerPK("acme", "power devil2"));
        assertNotNull(mower2);
        logger.info("found mower: {}", mower2);
        assertEquals(mower.getSize(), mower2.getSize());        

        em.remove(mower2);
        logger.info("removed mower: {}", mower2);
        em.flush();
        logger.info("removed mower after flush: {}", mower2);
        Mower mower3 = em.find(Mower.class, new MowerPK("acme", "power devil2"));
        assertNull(mower3);
        

        //leave a mower around for inspection
        Mower mower4 = new Mower("wack attack","bladerunner2"); 
        mower4.setSize(19);
        em.persist(mower4);
        logger.info("created leftover mower: {}", mower4);
    }
    
    /**
     * The Napsack class hosts an embedded primary key class, called
     * NapsackPK that is specific to the Napsack. All database mappings
     * are provided within the NapsackPK class.
     *
     */
    @Test
    public void testEmbeddedId() {
        logger.info("testEmbedded");
        ejava.examples.orm.core.annotated.Napsack napsack = 
            new Napsack("acme", "hold all2");
        napsack.setSize(3);
        
        //insert a row in the database
        em.persist(napsack);
        logger.info("created napsack: {}", napsack);
        
        Napsack napsack2 = 
            em.find(Napsack.class, new NapsackPK("acme", "hold all2"));
        assertNotNull(napsack2);
        logger.info("found napsack: {}", napsack2);
        assertEquals(napsack.getSize(), napsack2.getSize());
        
        napsack.setSize(30);
        assertEquals(napsack.getSize(), napsack2.getSize());
        logger.info("updated napsack: {}", napsack2);        
        
        em.remove(napsack);
        logger.info("removed napsack: {}", napsack2);

        //leave a object around for inspection
        em.flush();
        Napsack napsack3 = new Napsack("pack attack","getta round2"); 
        napsack3.setSize(19);
        em.persist(napsack3);
        logger.info("created leftover napsack: {}", napsack3);
    }        

    /**
     * The Pen class use a generic MakeModelPK class. All database mappings
     * are made within the Pen class and nothing is directly associated
     * with the generic PK class.
     *
     */
    @Test
    public void testEmbeddedIdOverrides() {
        logger.info("testEmbeddedOverrides");
        ejava.examples.orm.core.annotated.Pen pen = 
            new Pen("acme", "quick write2");
        pen.setSize(3);
        
        //insert a row in the database
        em.persist(pen);
        logger.info("created pen: {}", pen);
        
        Pen pen2 = 
            em.find(Pen.class, new MakeModelPK("acme", "quick write2"));
        assertNotNull(pen2);
        logger.info("found pen: {}", pen2);
        assertEquals(pen.getSize(), pen2.getSize());
        
        pen.setSize(30);
        assertEquals(pen.getSize(), pen2.getSize());
        logger.info("updated pen: {}", pen2);        
        
        em.remove(pen);
        logger.info("removed pen: {}", pen2);

        //leave a object around for inspection
        em.flush();
        Pen pen3 = new Pen("write attack","jotter2"); 
        pen3.setSize(19);
        em.persist(pen3);
        logger.info("created leftover pen: {}", pen3);
    }        
}
