package myorg.mypackage.ex1;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static Log log = LogFactory.getLog(AppTest.class);

    @Test
    public void testApp() {
        //System.out.println("testApp");
        log.info("testApp");
        App app = new App();
        assertTrue("app didn't return 1", app.returnOne() == 1);
    }

    //@Test
    public void testFail() {
        //System.out.println("testFail");
        log.info("testFail");
        App app = new App();
        assertTrue("app didn't return 0", app.returnOne() == 0);
    }
}
