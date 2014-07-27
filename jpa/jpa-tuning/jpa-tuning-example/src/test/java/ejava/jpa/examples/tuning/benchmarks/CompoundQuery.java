package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.MovieRating;

@TestLabel(label="Compound Query")
public class CompoundQuery extends TestBase {
	private static final String title[] = new String[]{
		"Beauty Shop",
		"Apollo 13",
		"Murder in the First",
		"A Few Good Men",
		"Cavedweller"
	};
	private static final Date releaseDate[] = new Date[]{
		new GregorianCalendar(2005, Calendar.JULY, 1).getTime(),	
		new GregorianCalendar(1995, Calendar.JULY, 1).getTime(),	
		new GregorianCalendar(1995, Calendar.JULY, 1).getTime(),	
		new GregorianCalendar(1992, Calendar.JULY, 1).getTime(),	
		new GregorianCalendar(2004, Calendar.JULY, 1).getTime()	
	};
	private static MovieRating rating[]=new MovieRating[]{
		MovieRating.PG13,
		MovieRating.PG,
		MovieRating.R,
		MovieRating.R,
		MovieRating.R
		
	};
	
	/**
	 * This test will query in the order of term1, term2 to test the impact of order on 
	 * an index.
	 */
	@TestLabel(label="Get By Term1 and Term2")
	@Test
	public void getByTerm1Term2() {
		for (int i=0; i<title.length; i++) {
			assertEquals(1,getDAO().getMoviesByTitleAndReleaseDate(title[i], releaseDate[i], null, null).size());
		}
	}

	/**
	 * This test will query in the order of term2, term1 to test the impact of order on
	 * an index.
	 */
	@TestLabel(label="Get By Term2 and Term1")
	@Test
	public void getByTerm2Term1() {
		for (int i=0; i<title.length; i++) {
			assertEquals(1,getDAO().getMoviesByReleaseDateAndTitle(title[i], releaseDate[i], null, null).size());
		}
	}
	
	/**
	 * This test will query for an additional term that is not part of the index
	 */
	@TestLabel(label="Get By Extra Term")
	@Test
	public void getByExtraTerm() {
		for (int i=0; i<title.length; i++) {
			assertEquals(1,getDAO().getMoviesByTitleAndReleaseDateAndRating(title[i], releaseDate[i], rating[i], null, null).size());
		}
	}
	
	/**
	 * This test will query for only the first term in the index(es). 
	 */
	@TestLabel(label="Get By First Term") 
	@Test
	public void getByFirstTerm() {
		for (int i=0; i<title.length; i++) {
			assertEquals(1,getDAO().getMoviesEqualsTitle(title[i], null, null).size());
		}
	}

	@TestLabel(label="Get By Second Term") 
	@Test
	public void getBySecondTerm() {
		for (int i=0; i<title.length; i++) {
			assertEquals(""+i, 1,getDAO().getMoviesByReleaseDate(releaseDate[i], 10, 1).size());
		}
	}

	@TestLabel(label="Get By Second and Extra Term") 
	@Test
	public void getBySecondAndExtraTerm() {
		for (int i=0; i<title.length; i++) {
			assertEquals(1,getDAO().getMoviesByReleaseDateAndRating(releaseDate[i], rating[i], 10, 1).size());
		}
	}
}