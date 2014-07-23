package ejava.examples.orm.core.products;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.orm.core.annotated.Tank;

/**
 * This test case provides a demo of using transient properties through
 * annotations.
 */
public class TransientAnnotationTest extends TestBase {
    private static Log log = LogFactory.getLog(BasicAnnotationTest.class);
    
    /**
     * This test provides a demo of persisting a class that has mapped
     * a getMakeModel() getter as @Transient so that it can be ignored
     * when persisting to the database.
     */
    @Test
    public void testTransient() {
        log.info("testTransient");
        ejava.examples.orm.core.annotated.Tank tank = new Tank(1);
        tank.setMake("acme");
        tank.setModel("great guns");

        //insert a row in the database
        em.persist(tank);
        log.info("created tank:" + tank);         
    }
    
}
