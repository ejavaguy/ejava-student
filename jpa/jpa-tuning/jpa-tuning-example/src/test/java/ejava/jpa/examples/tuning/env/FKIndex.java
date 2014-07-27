package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.ForeignKeys;

/**
 * This environment sets up the queries with indexes on foreign key column.
 */
@TestLabel(label="FKs Indexed")
public class FKIndex extends ForeignKeys {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		kevinBacon = getDAO().getKevinBacon();
		mf.createFKIndexes().flush();
		em.close();
	}
}
