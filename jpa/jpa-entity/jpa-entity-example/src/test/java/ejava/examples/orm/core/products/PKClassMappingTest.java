package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.orm.core.mapped.MakeModelPK;
import ejava.examples.orm.core.mapped.Mower;
import ejava.examples.orm.core.mapped.Napsack;
import ejava.examples.orm.core.mapped.NapsackPK;
import ejava.examples.orm.core.mapped.Pen;
import ejava.examples.orm.core.MowerPK;

/**
 * This test case provides a demo of using customer primary key classes
 * specified by annotations.
 */
public class PKClassMappingTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(BasicAnnotationTest.class);
    
    /**
     * This test provides a demo of using the AUTO GeneratedType. This value
     * is provided by the Java Persistence provider.
     */
    @Test
    public void testIdClass() {
        logger.info("testIdClass");
        ejava.examples.orm.core.mapped.Mower mower = 
            new Mower("acme", "power devil");
        mower.setSize(21);
        
        //insert a row in the database
        em.persist(mower);
        logger.info("created mower: {}", mower);
        
        Mower mower2 = 
            em.find(Mower.class, new MowerPK("acme", "power devil"));
        assertNotNull(mower2);
        logger.info("found mower: {}", mower2);
        assertEquals(mower.getSize(), mower2.getSize());
        
        mower.setSize(30);
        assertEquals(mower.getSize(), mower2.getSize());
        logger.info("updated mower: {}", mower2);        
        
        em.remove(mower);
        logger.info("removed mower: {}", mower2);

        //leave a mower around for inspection
        em.flush();
        Mower mower3 = new Mower("wack attack","bladerunner"); 
        mower3.setSize(19);
        em.persist(mower3);
        logger.info("created leftover mower: {}", mower3);
    }
    
    /**
     * The Napsack class hosts an embedded primary key class, called
     * NapsackPK that is specific to the Napsack. All database mappings
     * are provided within the NapsackPK class.
     */
    @Test
    public void testEmbeddedId() {
        logger.info("testEmbedded");
        ejava.examples.orm.core.mapped.Napsack napsack = 
            new Napsack("acme", "hold all");
        napsack.setSize(3);
        
        //insert a row in the database
        em.persist(napsack);
        logger.info("created napsack: {}", napsack);
        
        Napsack napsack2 = 
            em.find(Napsack.class, new NapsackPK("acme", "hold all"));
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
        Napsack napsack3 = new Napsack("pack attack","getta round"); 
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
        ejava.examples.orm.core.mapped.Pen pen = 
            new Pen("acme", "quick write");
        pen.setSize(3);
        
        //insert a row in the database
        em.persist(pen);
        logger.info("created pen: {}", pen);
        
        Pen pen2 = 
            em.find(Pen.class, new MakeModelPK("acme", "quick write"));
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
        Pen pen3 = new Pen("write attack","jotter"); 
        pen3.setSize(19);
        em.persist(pen3);
        logger.info("created leftover pen: {}", pen3);
    }        

}
