package ejava.jpa.examples.tuning.stuff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.annotation.AxisRange;

import ejava.jpa.examples.tuning.TestBase;


@Ignore
@AxisRange(min = 0, max = 4)
//@BenchmarkMethodChart(filePrefix="target/MyTestB")
public class MyTestB extends TestBase {
	private static final Logger log = LoggerFactory.getLogger(MyTestB.class);
	
	@BeforeClass
	public static void setUpClass() {
		log.trace("@BeforeClass.TestB");
	}
	
	@Before
	public void setUp() {
		log.trace("@Before.TestB");
	}

	@After
	public void tearDown() {
		log.trace("@After.TestB");
	}
	
	@AfterClass
	public static void tearDownClass() {
		log.trace("@AfterClass.TestB");
	}
	
	@Test public void test1(){ log.trace("@Test.TestB1"); }
	@Test public void test2(){ log.trace("@Test.TestB2"); }
}
