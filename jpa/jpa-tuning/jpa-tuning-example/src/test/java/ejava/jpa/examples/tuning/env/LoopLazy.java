package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.benchmarks.LoopQuery;

/**
 * This environment sets up the object model with all fetch=LAZY relationships.
 */
@TestLabel(label="Loop Lazy")
public class LoopLazy extends LoopQuery {
	private static String originalPU;
	
	@AfterClass
	public static void tearDownBenchmarkClass() {
	    PERSISTENCE_UNIT = originalPU;
	}

	@BeforeClass
	public static void setUpEnv() {
		TestBase.tearDownBaseClass();
		originalPU = PERSISTENCE_UNIT;
	    PERSISTENCE_UNIT = "movietune-test-thin";
        getDAO();
		TestBase.tearDownBaseClass();

		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_ROLE_ACTOR_MOVIE_CDX,
				mf.MOVIE_ROLE_MOVIE_ACTOR_CDX
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		
		em.close();
	}
}
