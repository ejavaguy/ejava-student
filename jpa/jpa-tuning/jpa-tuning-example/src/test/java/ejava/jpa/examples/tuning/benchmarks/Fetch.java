package ejava.jpa.examples.tuning.benchmarks;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.Movie;
import ejava.jpa.examples.tuning.bo.MovieRole;
import ejava.jpa.examples.tuning.dao.MovieDAOImpl;

/**
 * This test will obtain various object graphs to show the impact of EAGER and
 * LAZY fetch policies and actions.
 */
public class Fetch extends TestBase {
	protected static List<String> movieIds;
	protected MovieDAOImpl dao;
	
	@BeforeClass 
	public static void setUpClass() {
		movieIds = getDAO().getKevinBaconMovieIds(0, 20, "release_date ASC");
		log.info(movieIds.size() + " movies");
	}
	
	@Before
	public void setUp() {
		dao=getDAO();
	}
	
	@TestLabel(label="Get Parent")
	@Test
	public void getParent() {
		for (String id: movieIds) {
			Movie m = dao.getMovieById(id);
			m.getTitle();
		}
	}

	@TestLabel(label="Get Just Parent")
	@Test
	public void getJustParent() {
		for (String id: movieIds) {
			Movie m = dao.getMovieByIdUnfetched(id);
			m.getTitle();
		}
	}
	
	@TestLabel(label="Get Parent and Children")
	@Test
	public void getParentAndChildren() {
		for (String id: movieIds) {
			Movie m = dao.getMovieById(id);
			if (m.getDirector()!=null) { m.getDirector().getPerson().getLastName(); }
			m.getGenres().iterator().next();
			for (MovieRole role: m.getCast()) {
				if (role.getActor()!=null) { role.getActor().getPerson().getLastName(); }
			}
		}
	}

	@TestLabel(label="Get Parent with Fetched Children")
	@Test
	public void getParentFetchChildren() {
		for (String id: movieIds) {
			Movie m = dao.getMovieFetchedByIdFetched(id);
			if (m.getDirector()!=null) { m.getDirector().getPerson().getLastName(); }
			m.getGenres().iterator().next();
			for (MovieRole role: m.getCast()) {
				if (role.getActor()!=null) { role.getActor().getPerson().getLastName(); }
			}
		}
	}
}