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
@AxisRange(min = 0, max = 1)
//@BenchmarkMethodChart(filePrefix="target/MyTestA")
public class MyTestA extends TestBase {
	private static final Logger log = LoggerFactory.getLogger(MyTestA.class);
	
	@BeforeClass
	public static void setUpClass() {
		log.trace("@BeforeClass.TestA");
	}
	
	@Before
	public void setUp() {
		log.trace("@Before.TestA");
	}

	@After
	public void tearDown() {
		log.trace("@After.TestA");
	}
	
	@AfterClass
	public static void tearDownClass() {
		log.trace("@AfterClass.TestA");
	}
	
	@Test public void test1(){ log.trace("@Test.TestA1"); }
	@Test public void test2(){ log.trace("@Test.TestA2"); }
}
