package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.orm.core.ColorType;
import ejava.examples.orm.core.mapped.Vase;

/**
 * This test case provides an example of providing special mappings for
 * certain types, like dates and enums.
 */
public class TypesMappingTest extends TestBase {
    private static Log log = LogFactory.getLog(TypesMappingTest.class);
    
    /**
     */
    @Test
    public void testValues() {
        log.info("testValues");
        ejava.examples.orm.core.mapped.Vase vase = new Vase(2);
        Date date = new Date();
        vase.setADate(date);
        vase.setATime(date);
        vase.setATimestamp(date);
        vase.setColorId(ColorType.RED);
        vase.setColorName(ColorType.RED);
        
        //insert a row in the database
        em.persist(vase);
        log.info("created case:" + vase);
        
        //find the inserted object
        em.flush();
        em.clear();
        Vase vase2 = em.find(Vase.class, 2L); 

        log.info("found vase:" + vase2);
        assertNotNull(vase2);
        assertEquals(vase2.getADate(), vase2.getADate());
        assertEquals(vase2.getATime(), vase2.getATime());
        assertEquals(vase2.getATimestamp(), vase2.getATimestamp());
        assertEquals(vase2.getColorId(), vase2.getColorId());
        assertEquals(vase2.getColorName(), vase2.getColorName());        
    }        
}
