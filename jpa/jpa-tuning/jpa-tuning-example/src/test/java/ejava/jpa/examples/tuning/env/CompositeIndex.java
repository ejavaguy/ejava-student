package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.CompoundQuery;

/**
 * This setup creates an environment where both terms in the where clause are
 * part of a composite index.
 */
@TestLabel(label="Compound Index")
public class CompositeIndex extends CompoundQuery {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_TITLE_RDATE_IDX,
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}
}
