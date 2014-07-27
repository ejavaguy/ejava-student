package ejava.jpa.examples.tuning.stuff;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
	MyTestA.class,
	MyTestB.class
})
public class MyTestSuiteTest {
	private static final Log log = LogFactory.getLog(MyTestSuiteTest.class);
	
	@BeforeClass
	public static void setUpClass() {
		log.info("@BeforeClass.Suite");
		System.setProperty("jub.customkey", "MyTestSuiteTest");
	}

	@AfterClass
	public static void tearDownClass() {
		log.info("@AfterClass.Suite");
	}

	@Before public void setUp() { log.info("@Before.Suite -- not called"); }
	@After public void tearDown() { log.info("@After.Suite -- not called"); }
}
