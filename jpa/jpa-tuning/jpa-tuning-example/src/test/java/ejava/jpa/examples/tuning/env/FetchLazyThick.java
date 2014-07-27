package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.benchmarks.Fetch;

/**
 * This environment sets up the object model with all fetch=LAZY relationships
 * and the plot field mapped.
 */
@TestLabel(label="Fetch Lazy Thick Parent")
public class FetchLazyThick extends Fetch {
	private static String originalPU;
	
	@AfterClass
	public static void tearDownBenchmarkClass() {
	    PERSISTENCE_UNIT = originalPU;
	}

	@BeforeClass
	public static void setUpEnv() {
		TestBase.tearDownBaseClass();
		originalPU = PERSISTENCE_UNIT;
	    PERSISTENCE_UNIT = "movietune-test";
        getDAO();
		TestBase.tearDownBaseClass();

		for (String id: movieIds) {
			assertNotNull("null plot", getDAO().getMovieById(id).getPlot());
		}
		
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_ROLE_MOVIE_FDX,
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		
		em.close();
	}
}
