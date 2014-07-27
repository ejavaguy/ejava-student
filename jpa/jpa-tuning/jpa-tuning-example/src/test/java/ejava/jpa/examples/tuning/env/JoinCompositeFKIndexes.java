package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;
import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.benchmarks.JoinQuery;

/**
 * This test environment sets up the queries with separate table and FK indexes.
 */
@TestLabel(label="Composite with FK Index")
public class JoinCompositeFKIndexes extends JoinQuery {
	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_ROLE_MOVIE_CDX
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		new MovieFactory().setEntityManager(em).flush();
		em.close();
	}
}
