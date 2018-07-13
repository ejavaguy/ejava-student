package ejava.examples.webtier.bl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the main entry point for each of the individual test
 * cases. It provides one-time setUp and tearDown functionality needed by 
 * all individual test cases.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        //list of classes in test suite
    BusinessLogicDemo.class
})
public class AllTest {
    private static final Logger logger = LoggerFactory.getLogger(AllTest.class);

    @BeforeClass
    public static void setUpSuite() {
        logger.info("anything setup before suite");
    }

    @AfterClass
    public static void tearDoanSuite() {
        logger.info("anything torn down after suite");
    }
}
