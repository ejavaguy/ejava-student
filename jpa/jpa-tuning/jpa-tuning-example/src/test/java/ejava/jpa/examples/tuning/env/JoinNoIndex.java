package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;
import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.JoinQuery;

/**
 * This test environment sets up the queries with no indexes.
 */
@TestLabel(label="No Table or FK Indexes")
public class JoinNoIndex extends JoinQuery {
	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		new MovieFactory().setEntityManager(em).flush();
		em.close();
	}
}
