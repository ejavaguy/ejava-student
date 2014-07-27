package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;
import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.benchmarks.ForeignKeys;

/**
 * This environment sets up the queries with indexes on foreign keys between tables.
 */
@TestLabel(label="FKs, Where, and Join Indexed")
public class AllFKIndex extends ForeignKeys {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		kevinBacon = getDAO().getKevinBacon();
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_TITLE_RDATE_ID_IDX
		};
		mf.executeSQL(constructs).createFKIndexes().flush();
		em.close();
	}
}
