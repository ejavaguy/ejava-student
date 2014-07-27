package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;

import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.ForeignKeys;

/**
 * This test environment sets up the queries with no indexes.
 */
@TestLabel(label="No Indexes")
public class NoFKIndex extends ForeignKeys {
	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		kevinBacon = getDAO().getKevinBacon();
		new MovieFactory().setEntityManager(em).flush();
		em.close();
	}

}
