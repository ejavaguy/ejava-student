package myorg.queryex;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class QueryTest extends QueryBase {
	private static final Log log = LogFactory.getLog(QueryTest.class);
	
	/**
	 * This test method demonstrates retrieving zero to many matching entities
	 * from a database.
	 */
	@Test
	public void testMulti() {
		log.info("*** testMulti ***");
		
		List<Movie> movies = em.createQuery(
				"select m from Movie m " +
				"order by title ASC", Movie.class)
				.getResultList();
		log.debug("result=" + movies);
		assertEquals("unexpected number of movies", 7, movies.size());
	}

	/**
	 * This test method demonstrates retrieving a single result from the 
	 * database when there is a single row that matches.
	 */
	@Test
	public void testSingle() {
		log.info("*** testSingle ***");
		
		Movie movie = em.createQuery(
				"select m from Movie m " +
				"where m.title='Animal House'", Movie.class)
				.getSingleResult();
		log.debug("result=" + movie);
		assertNotNull("no movie", movie);
	}

	/**
	 * This test method demonstrates the exception that is thrown when 
	 * no matching rows exist in the database when asking for a single result.
	 */
	@Test(expected=NoResultException.class)
	public void testSingleNoResult() {
		log.info("*** testSingleNoResult ***");
		
		em.createQuery(
				"select m from Movie m " +
				"where m.title='Animal Hut'", Movie.class)
				.getSingleResult();
		log.debug("query did not produce expected exception");
	}

	/**
	 * This test method demonstrates the exception that is thrown when 
	 * multiple matching rows exist in the database when asking for a single result.
	 */
	@Test(expected=NonUniqueResultException.class)
	public void testSingleNonUniqueResult() {
		log.info("*** testSingleNonUniqueResult ***");
		
		em.createQuery(
				"select m from Movie m " +
				"where m.rating='R'", Movie.class)
				.getSingleResult();
		log.debug("query did not produce expected exception");
	}

	/**
	 * This test method demonstrates the ability to pass in parameters to a 
	 * query.
	 */
	@Test
	public void testParameters() {
		log.info("*** testParameters ***");
		
		List<Movie> movies = em.createQuery(
				"select m from Movie m " +
				"where m.rating=:rating " +
				"and m.releaseDate > :date " +
				"and lower(m.title) like concat(concat('%',lower(:title)),'%')", Movie.class)
				.setParameter("rating", MovieRating.R)
				.setParameter("date", new GregorianCalendar(1980, 0, 0).getTime(), TemporalType.DATE)
				.setParameter("title", "wag")
				.getResultList();
		log.debug("result=" + movies);
		assertEquals("unexpected number of movies", 1, movies.size());
	}

	/**
	 * This test method demonstrates the ability to control the number of results 
	 * returned from a query and to page through those results.
	 */
	@Test
	public void testPaging() {
		log.info("*** testPaging ***");

		List<Movie> movies = new LinkedList<Movie>();
		TypedQuery<Movie> query = em.createQuery(
				"select m from Movie m " +
				"order by title", Movie.class)
				.setMaxResults(3);
		List<Movie> page=null;
		int offset=0;
		do {
			page = query.setFirstResult(offset).getResultList();
			log.debug("page=" + page);
			movies.addAll(page);
			offset += page.size();
			log.debug("page.size=" + page.size() + ", offset=" + offset);
		} while (page.size() > 0);
		
		log.debug("result=" + movies);
		assertEquals("unexpected number of movies", 7, movies.size());
	}
	
	/**
	 * This test method demonstrates passing a query hint to the provider for  
	 * the query execution.
	 */
	@Test
	public void testNamedQuery() {
		log.info("*** testNamedQuery ***");
		
		Movie movie = em.createNamedQuery("Movie.findByTitle", Movie.class)
				.setParameter("title", "%wag%")
				.getSingleResult();
		log.debug("result=" + movie);
		assertNotNull("no movie", movie);
	}
	
	/**
	 * This test method demonstrates retrieving values from a query and 
	 * not the complete managed entity.
	 */
	@Test
	public void testValueQuery() {
		log.info("*** testValueQuery ***");
	
		List<String> titles = em.createQuery(
				"select m.title from Movie m " +
				"order by title ASC", String.class)
				.getResultList();
		for (String title : titles) {
			log.debug(title);
		}
		assertEquals("unexpected number of titles", 7, titles.size());
	}
	
	/**
	 * This test method demonstrates retrieving the value result of a query 
	 * function
	 */
	@Test
	public void testResultValueQuery() {
		log.info("*** testResultValueQuery ***");

		int titleCount = em.createQuery(
				"select count(m) from Movie m", Number.class)
				.getSingleResult().intValue();
		log.debug("titleCount=" + titleCount);
		assertEquals("unexpected number of titles", 7, titleCount);
	}
	
	/**
	 * This test method demonstrates retrieving multiple values from a query.
	 */
	@Test
	public void testMultiValueQuery() {
		log.info("*** testMultiValueQuery ***");

		List<Object[]> results = em.createQuery(
				"select m.title, m.releaseDate from Movie m " +
				"order by title ASC", Object[].class)
				.getResultList();
		for (Object[] result : results) {
			String title = (String)result[0];
			Date releaseDate = (Date)result[1];
			log.debug(String.format("%s (%s)", title, releaseDate));
		}
		assertEquals("unexpected number of results", 7, results.size());
	}
	
    private static class MovieRelease {
    	public final String title;
    	public final Date releaseDate;
    	@SuppressWarnings("unused")
		public MovieRelease(String title, Date releaseDate) {
    		this.title = title;
    		this.releaseDate = releaseDate;
    	}
    }

    /**
     * This test method demonstrates the ability to create a result class to 
     * encapsulate the results returned from a value query.
     */
    @Test
	public void testResultClass() {
		log.info("*** testResultClass ***");

		String query = String.format("select new %s(m.title, m.releaseDate) " +
				"from Movie m order by title ASC", 
				MovieRelease.class.getName());
		log.debug(query);
		List<MovieRelease> results = em.createQuery(query, MovieRelease.class)
				.getResultList();
		for (MovieRelease movie: results) {
			log.debug(String.format("%s (%s)", movie.title, movie.releaseDate));
		}
		assertEquals("unexpected number of results", 7, results.size());
	}
	
}
