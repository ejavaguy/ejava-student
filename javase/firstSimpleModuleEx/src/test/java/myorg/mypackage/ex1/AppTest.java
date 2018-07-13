package myorg.mypackage.ex1;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for simple App.
 */
public class AppTest {
    private static Logger logger = LoggerFactory.getLogger(AppTest.class);

    @Test
    public void testApp() {
        //System.out.println("testApp");
        logger.info("testApp");
        App app = new App();
        assertTrue("app didn't return 1", app.returnOne() == 1);
    }

    //@Test
    public void testFail() {
        //System.out.println("testFail");
        logger.info("testFail");
        App app = new App();
        assertTrue("app didn't return 0", app.returnOne() == 0);
    }
}
