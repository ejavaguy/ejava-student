package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;

import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.FullTableScan;

@TestLabel(label="Table Access with Single Indexes")
public class FullTableScanSingleIndex extends FullTableScan {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_RATING_IDX,
				mf.MOVIE_RATING_LOWER_IDX,
				mf.MOVIE_TITLE_IDX,
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}
}
