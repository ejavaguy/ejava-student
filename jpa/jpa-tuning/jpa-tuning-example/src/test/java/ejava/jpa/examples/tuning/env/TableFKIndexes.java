package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;
import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.benchmarks.ForeignKeys;

/**
 * This environment sets up the queries with indexes on foreign keys and columns
 * without adding the ID to the composite index
 */
@TestLabel(label="FKs and Where Indexed")
public class TableFKIndexes extends ForeignKeys {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_TITLE_RDATE_IDX
		};
		mf.executeSQL(constructs).createFKIndexes().assertConstructs(constructs).flush();
		em.close();
	}
}
