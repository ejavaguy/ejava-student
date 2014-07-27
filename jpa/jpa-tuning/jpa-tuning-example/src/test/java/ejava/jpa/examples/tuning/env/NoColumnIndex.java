package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.ValueQuery;

/**
 * This environment sets up the queries with no column indexes.
 */
@TestLabel(label="No Column Indexes")
public class NoColumnIndex extends ValueQuery {
	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		new MovieFactory().setEntityManager(em).flush();
		em.close();
	}

}
