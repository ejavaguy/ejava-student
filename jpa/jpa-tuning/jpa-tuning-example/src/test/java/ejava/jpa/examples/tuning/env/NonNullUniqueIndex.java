package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.ByIndex;

@TestLabel(label="Non-Null Uniuque Index")
public class NonNullUniqueIndex extends ByIndex {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_UTITLE,
				mf.MOVIE_UTITLE_NONNULL,
				mf.MOVIE_UTITLE_UDX
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}
}
