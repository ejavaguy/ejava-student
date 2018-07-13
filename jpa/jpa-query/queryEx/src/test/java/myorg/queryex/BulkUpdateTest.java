package myorg.queryex;

import static org.junit.Assert.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

public class BulkUpdateTest extends QueryBase {
    private static final Logger log = LoggerFactory.getLogger(BulkUpdateTest.class);
	public static enum Action { INSERT, UPDATE, FAIL };
	
	@Before
	public void setUpLocksTest() {
		em.getTransaction().commit();
		cleanup(em);
		populate(em);
		em.getTransaction().begin();
	}
	
	/**
	 * This test will demonstrate the ability to use JPAQL to perform a bulk 
	 * update directly within the database.
	 */
	@Test
	public void testUpdate() {
		log.info("*** testUpdate ***");
		
		log.debug("get a copy of the entity into the persistence context cache for demo");
		String oldFirst = "Ron";
		String oldLast = "Howard";
		Director d = em.createQuery("select d from Director d JOIN FETCH d.person p " +
				"where p.firstName=:firstName and p.lastName=:lastName", Director.class)
				.setParameter("firstName", oldFirst)
				.setParameter("lastName", oldLast)
				.getSingleResult();
		log.debug("entity in cache=" + d);
		
		String newFirst = "Opie";
		String newLast = "Taylor";
		log.debug("performing bulk update");
		int changes=em.createQuery("update Person p " +
				"set p.firstName=:newFirst, p.lastName=:newLast " +
				"where p.firstName=:oldFirst and p.lastName=:oldLast")
				.setParameter("newFirst", newFirst)
				.setParameter("newLast", newLast)
				.setParameter("oldFirst", oldFirst)
				.setParameter("oldLast", oldLast)
				.executeUpdate();
		log.debug("changes=" + changes);
		assertEquals("unexpected changes", 1, changes);
		log.debug("entity still in cache has old values=" + d);
		assertEquals("unexpected cache change", oldFirst, d.getFirstName());
		assertEquals("unexpected cache change", oldLast, d.getLastName());

		log.debug("refreshing cache with changes to database");
		em.refresh(d.getPerson());
		log.debug("refreshed entity in cache has new values=" + d);
		assertEquals("unexpected cache change", newFirst, d.getFirstName());
		assertEquals("unexpected cache change", newLast, d.getLastName());
	}

	/**
	 * This test will demonstrate using a native SQL command within a JPA
	 * bulk update.
	 */
	@Test	
	public void testSQLUpdate() {
		log.info("*** testSQLUpdate ***");
		
		log.debug("get a copies of the entities into the persistence context cache for demo");
		String genre="Crime Drama";
		@SuppressWarnings("unchecked")
		List<Movie> movies = em.createQuery("select m from Movie m JOIN m.genres g " +
				"where g = :genre")
				.setParameter("genre", genre)
				.getResultList();
		int genreCount=0;
		for (Movie movie : movies) {
			log.debug("entity in cache=" + movie + ", genres=" + movie.getGenres());
			genreCount += movie.getGenres().contains(genre)?1:0;
		}
		assertTrue("no movies found for genre", movies.size()>0);
		assertTrue("unexpected genre count", genreCount > 0);
		
		log.debug("performing bulk update to remove genre=" + genre);
		int changes=em.createNativeQuery("delete from QUERYEX_MOVIEGENRE g " +
				"where g.genre=?1")
				.setParameter(1, genre)
				.executeUpdate();
		log.debug("changes=" + changes);
		assertEquals("unexpected changes", 1, changes);
		for (Movie movie : movies) {
			log.debug("entity still in cache=" + movie + ", genres=" + movie.getGenres());
		}
		
		log.debug("refreshing cached objects");
		genreCount=0;
		for (Movie movie : movies) {
			em.refresh(movie);
			log.debug("entity in cache=" + movie + ", genres=" + movie.getGenres());
			genreCount += movie.getGenres().contains(genre)?1:0;
		}
		assertEquals("unexpected cache change", 0, genreCount);
	}
}
