package ejava.jpa.examples.tuning;

import org.junit.AfterClass;

import ejava.jpa.examples.tuning.TestBase;

public class SuiteBase {
	@AfterClass
	public static void tearDownClass() {
		TestBase.printResults();
	}
}
