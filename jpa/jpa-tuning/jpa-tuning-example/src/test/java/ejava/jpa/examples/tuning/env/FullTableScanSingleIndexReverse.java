package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;

import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.FullTableScan;

/**
 * Creates reverse indexes for tests to work with
 */
@TestLabel(label="Table Access with Single Indexes DESC")
public class FullTableScanSingleIndexReverse extends FullTableScan {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_RATING_RIDX,
				mf.MOVIE_RATING_LOWER_RIDX,
				mf.MOVIE_TITLE_RIDX,
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}
}
