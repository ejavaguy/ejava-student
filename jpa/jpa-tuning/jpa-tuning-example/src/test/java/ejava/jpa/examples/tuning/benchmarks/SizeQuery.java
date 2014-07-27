package ejava.jpa.examples.tuning.benchmarks;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.dao.MovieDAOImpl;

/**
 * This test will perform counts using multiple techniques.
 */
public class SizeQuery extends TestBase {
	protected static List<String> movieIds;
	protected MovieDAOImpl dao;
	
	@BeforeClass 
	public static void setUpClass() {
		movieIds = getDAO().getKevinBaconMovieIds(0, 40, "release_date ASC");
		log.info(movieIds.size() + " movies");
	}
	
	@Before
	public void setUp() {
		dao=getDAO();
	}
	
	
	@TestLabel(label="DAO Relation Count")
	@Test
	public void countByDAORelation() {
		for (String movieId: movieIds) {
			dao.getMovieCastCountByDAORelation(movieId);
		}
	}

	@TestLabel(label="DAO Count")
	@Test
	public void countByDAO() {
		for (String movieId: movieIds) {
			dao.getMovieCastCountByDAO(movieId);
		}
	}
	
	@TestLabel(label="DB Count")
	@Test
	public void countByDB() {
		for (String movieId: movieIds) {
			dao.getMovieCastCountByDB(movieId);
		}
	}

	@TestLabel(label="DB Count no Join")
	@Test
	public void countByDbNoJoin() {
		for (String movieId: movieIds) {
			dao.getCastCountForMovie(movieId);
		}
	}

}