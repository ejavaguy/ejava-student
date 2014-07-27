package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;

@TestLabel(label="Table Access")
public class ByIndex extends TestBase {
	private static String originalPU;
	@BeforeClass
	public static void setUpBenchmarkClass() {
		TestBase.tearDownBaseClass();
		originalPU = PERSISTENCE_UNIT;
	    PERSISTENCE_UNIT = "movietune-test-utitle";
        getDAO();
		TestBase.tearDownBaseClass();
	}
	
	@AfterClass
	public static void tearDownBenchmarkClass() {
	    PERSISTENCE_UNIT = originalPU;
	}
	
	/**
	 * This will demonstrate how the unique-ness and non-null aspects of an index impacts
	 * a query plan.
	 */
	@TestLabel(label="Values By Index")
	@Test
	public void valuesByIndex() {
		assertEquals(1,getDAO().getMoviesEqualsTitle("Tremors(m836199)", null, null).size());
	}

	@TestLabel(label="Values By Index (limit=1)")
	@Test
	public void valuesByIndexMaxOne() {
		assertEquals(1,getDAO().getMoviesEqualsTitle("Tremors(m836199)", null, 1).size());
	}
}