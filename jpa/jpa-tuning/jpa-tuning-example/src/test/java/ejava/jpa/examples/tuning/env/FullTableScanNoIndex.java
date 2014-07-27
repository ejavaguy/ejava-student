package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;

import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.FullTableScan;

@TestLabel(label="Table Access without Index")
public class FullTableScanNoIndex extends FullTableScan {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		mf.flush();
		em.close();
	}
}
