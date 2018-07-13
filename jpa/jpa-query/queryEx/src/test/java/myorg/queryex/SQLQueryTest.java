package myorg.queryex;

import static org.junit.Assert.*;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class SQLQueryTest extends QueryBase {
    private static final Logger log = LoggerFactory.getLogger(SQLQueryTest.class);
    
    /**
     * This test method demonstrates building and executing a SQL query using 
     * the entity manager.
     */
    @Test
    public void testSQLQuery() {
    	log.info("*** testSQLQuery ***");    	
    	@SuppressWarnings("unchecked")
		List<String> titles = em.createNativeQuery(
    			"select title from queryex_movie " +
    			"order by title ASC").getResultList();
    	for (String title : titles) {
    		log.debug(title);
    	}
    	assertEquals("unexpected number of titles", 7, titles.size());
    }
    
    /**
     * The test method demonstrates using a custom SQL query to derive the 
     * values used to populate a JPA entity class. The columns returned must
     * match the columns expected by the provider or we must use a
     * @SQLResultSetMapping. In this case -- we are returning a single entity
     * and can simply specify the entity and have the mappings taken from the 
     * entity class' JPA annotations.  
     */
    @Test
    public void testSQLResultMapping() {
    	log.info("*** testSQLResultMapping ***");    	
    	@SuppressWarnings("unchecked")
		List<Movie> movies = em.createNativeQuery(
    			"select m.* from queryex_movie m " +
    			"join queryex_person p on p.id = m.director_id " +
    			"where p.first_name = 'Ron'" +
    			"order by title ASC", Movie.class).getResultList();
    	log.debug("result=" + movies);
    	for (Movie movie: movies) {
    		log.debug("em.contains(" + movie + ")=" + em.contains(movie));
    		assertTrue(movie + " not managed", em.contains(movie));
    	}
    	assertEquals("unexpected number of movies", 2, movies.size());
    	
    	log.debug("checking unmapped entity name");
    	assertEquals("unexpected director first name", 
    			"Ron", movies.get(0).getDirector().getPerson().getFirstName());
    }

    /**
     * This test method provides an example of using SQLResultSetMapping to 
     * define more than one returned entity. Default names for each of the columns
     * are being returned here. This will cause some ambiguity with two of the 
     * entities and require some refinement in the next two test methods.
     */
    @Test
    public void testSQLMultiResultMapping() {
    	log.info("*** testSQLMultiResultMapping ***");    	
    	@SuppressWarnings("unchecked")
		List<Object[]> results = em.createNativeQuery(
    			"select * " +
    			"from queryex_movie m " +
    			"join queryex_director dir on dir.person_id = m.director_id " +
    			"join queryex_person p on p.id = dir.person_id " +
    			"where p.first_name = 'Ron'" +
    			"order by title ASC", "Movie.movieMapping").getResultList();
		log.debug("query returned " + results.size() + " results");
    	for (Object[] result: results) {
    		Movie movie = (Movie)result[0];
    		Director director = (Director) result[1];
    		Person person = (Person)result[2];
    		log.debug("em.contains(" + movie + ")=" + em.contains(movie));
    		log.debug("em.contains(" + director + ")=" + em.contains(director));
    		log.debug("em.contains(" + person + ")=" + em.contains(person));
    		assertTrue(movie + " not managed", em.contains(movie));
    		assertTrue(director + " not managed", em.contains(director));
    		assertTrue(person + " not managed", em.contains(person));
    	}
    	assertEquals("unexpected number of movies", 2, results.size());

    	log.debug("checking unmapped entity name");
    	assertEquals("unexpected director first name", 
    			"Ron", ((Movie)((Object[])results.get(0))[0]).getDirector().getPerson().getFirstName());
    }

    /**
     * This test method is a slight refinement of the test method above in that 
     * is explicitly names each table-alias.column that gets returned by the select.
     * Using this explicit query makes it easier for us to spot the ambiguity 
     * between MOVIE.ID and PERSON.ID. 
     */
    @Test
    public void testSQLMultiResultMapping1() {
    	log.info("*** testSQLMultiResultMapping ***");    	
    	@SuppressWarnings("unchecked")
		List<Object[]> results = em.createNativeQuery(
    			"select " +
    					"m.id, m.minutes, m.rating, m.release_date, m.title, m.director_id, " +
    					"dir.person_id, " +
    					"p.id, p.first_name, p.last_name, p.birth_date " +
    			"from queryex_movie m " +
    			"join queryex_director dir on dir.person_id = m.director_id " +
    			"join queryex_person p on p.id = dir.person_id " +
    			"where p.first_name = 'Ron'" +
    			"order by title ASC", "Movie.movieMapping").getResultList();
		log.debug("query returned " + results.size() + " results");
    	for (Object[] result: results) {
    		Movie movie = (Movie)result[0];
    		Director director = (Director) result[1];
    		Person person = (Person)result[2];
    		log.debug("em.contains(" + movie + ")=" + em.contains(movie));
    		log.debug("em.contains(" + director + ")=" + em.contains(director));
    		log.debug("em.contains(" + person + ")=" + em.contains(person));
    		assertTrue(movie + " not managed", em.contains(movie));
    		assertTrue(director + " not managed", em.contains(director));
    		assertTrue(person + " not managed", em.contains(person));
    	}
    	assertEquals("unexpected number of movies", 2, results.size());

    	log.debug("checking unmapped entity name");
    	assertEquals("unexpected director first name", 
    			"Ron", ((Movie)((Object[])results.get(0))[0]).getDirector().getPerson().getFirstName());
    }

    /**
     * This test method provides the final refinement of the above two test methods --
     * where the second of two tables is aliased so there is no ambiguity between
     * the two tables. This alias is registered in the field mapping of the SqlResultMapping
     * for the particular @EntityResult.
     */
    @Test
    public void testSQLMultiResultMapping2() {
    	log.info("*** testSQLMultiResultMapping ***");    	
    	@SuppressWarnings("unchecked")
		List<Object[]> results = em.createNativeQuery(
    			"select " +
    					"m.id, m.minutes, m.rating, m.release_date, m.title, m.director_id, " +
    					"dir.person_id, " +
    					"p.id as p_id, " + //NOTICE: the alias for PERSON.ID
    					"p.first_name, p.last_name, p.birth_date " +
    			"from queryex_movie m " +
    			"join queryex_director dir on dir.person_id = m.director_id " +
    			"join queryex_person p on p.id = dir.person_id " +
    			"where p.first_name = 'Ron'" +
    			"order by title ASC", "Movie.movieMapping2").getResultList();
		log.debug("query returned " + results.size() + " results");
    	for (Object[] result: results) {
    		Movie movie = (Movie)result[0];
    		Director director = (Director) result[1];
    		Person person = (Person)result[2];
    		log.debug("em.contains(" + movie + ")=" + em.contains(movie));
    		log.debug("em.contains(" + director + ")=" + em.contains(director));
    		log.debug("em.contains(" + person + ")=" + em.contains(person));
    		assertTrue(movie + " not managed", em.contains(movie));
    		assertTrue(director + " not managed", em.contains(director));
    		assertTrue(person + " not managed", em.contains(person));
    	}
    	assertEquals("unexpected number of movies", 2, results.size());

    	log.debug("checking unmapped entity name");
    	assertEquals("unexpected director first name", 
    			"Ron", ((Movie)((Object[])results.get(0))[0]).getDirector().getPerson().getFirstName());
    }
}
