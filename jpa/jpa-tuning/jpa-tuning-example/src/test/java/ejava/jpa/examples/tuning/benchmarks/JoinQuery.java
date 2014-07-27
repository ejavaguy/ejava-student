package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;

import org.junit.Test;
import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;

/**
 * This set of tests demonstrates queries over table joins
 */
@TestLabel(label="Table Join")
public class JoinQuery extends TestBase {
	
	@TestLabel(label="Small Driving Table")
	@Test
	public void smallDrivingTable() {
		assertEquals(2,getDAO().getMoviesByRole("Valentine McKee", 0, 2, null).size());
	}

	@TestLabel(label="Large Driving Table (Bounded)")
	@Test
	public void largeDrivingTableBounded() {
		assertEquals(2,getDAO().getMoviesByLikeRole("V%", 0, 2, null).size());
	}

	@TestLabel(label="Large Driving Table Offset (Bounded)")
	@Test
	public void largeDrivingTableOffsetBounded() {
		assertEquals(2,getDAO().getMoviesByLikeRole("V%", 1000, 2, null).size());
	}

	@TestLabel(label="Large Driving Table (Unbounded")
	@Test
	public void largeDrivingTableUnbounded() {
		getDAO().getMoviesByLikeRole("V%", null, null, null);
	}
}
