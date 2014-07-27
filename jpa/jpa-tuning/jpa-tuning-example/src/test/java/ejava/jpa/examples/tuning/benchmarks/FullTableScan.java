package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;

import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.MovieRating;
import ejava.jpa.examples.tuning.suites.FullTableScanTest;

@TestLabel(label="Table Access")
public class FullTableScan extends TestBase {
	private static int MAX_ROWS=FullTableScanTest.MAX_ROWS;
	
	/**
	 * This test shows the speed at which a full table scan can work when there
	 * are no constraints on the rows returned.
	 */
	@TestLabel(label="Unrestricted Scan")
	@Test
	public void unrestrictedScan() {
		assertEquals(MAX_ROWS,getDAO().getMovies(0,MAX_ROWS, null).size());
	}
	
	/**
	 * This test demonstrates the speed at which a page of rows are returned when the
	 * rows are selected by a criteria on an indexed column. 
	 */
	@TestLabel(label="Value Access")
	@Test
	public void valueAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingValue(MovieRating.R, 0,MAX_ROWS, null).size());
	}

	/**
	 * This test demonstrates the impact of calling a function on row data within 
	 * a query -- invalidating any data index created on that column.
	 */
	@TestLabel(label="Unindexed Function Access")
	@Test
	public void unindexedFunctionAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingUpperFunction(MovieRating.R, 0,MAX_ROWS).size());
	}
	
	/**
	 * This test demonstrates the impact of adding a function index on a column
	 * that matches the function used on row data within the query
	 */
	@TestLabel(label="Indexed Function Access")
	@Test
	public void indexedFunctionAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingLowerFunction(MovieRating.R, 0,MAX_ROWS).size());
	}

	@TestLabel(label="OrderBy Non-Null Column")
	@Test
	public void orderByUsingNonNullColumn() {
		assertEquals(MAX_ROWS,getDAO().getMovies(0,MAX_ROWS, "title ASC").size());
	}
	
	@TestLabel(label="OrderBy Nullable Column")
	@Test
	public void orderByUsingNullableColumn() {
		assertEquals(MAX_ROWS,getDAO().getMovies(0,MAX_ROWS, "rating ASC").size());
	}
	
	@TestLabel(label="OrderBy Non-null Non-Where Column")
	@Test
	public void orderByUsingNonNullNonWhereColumn() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingValue(MovieRating.R, 0,MAX_ROWS,"title ASC").size());
	}

	@TestLabel(label="OrderBy Non-null Where Column ASC")
	@Test
	public void orderByUsingNonNullWhereColumnAsc() {
		assertEquals(MAX_ROWS,getDAO().getMoviesLikeTitle("A%", 0, MAX_ROWS, "title ASC").size());
	}
	
	@TestLabel(label="OrderBy Non-null Where Column DESC")
	@Test
	public void orderByUsingNonNullWhereColumnDesc() {
		assertEquals(MAX_ROWS,getDAO().getMoviesLikeTitle("A%", 0, MAX_ROWS, "title DESC").size());
	}
	
	/**
	 * This test demonstrates how a trailing wildcard does not invalidate an index
	 */
	@TestLabel(label="Ending Wildcard")
	@Test
	public void endingWildcard() {
		assertEquals(1,getDAO().getMoviesLikeTitle("Natural Disasters: Forces of Nature%", 0, MAX_ROWS, null).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Mystic River: From Page to Scree%", 0, MAX_ROWS, null).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Seventeen: The Faces for Fal%", 0, MAX_ROWS, null).size());
	}

	/**
	 * This test demonstrates how a leading wildcard invalidates an index
	 */
	@TestLabel(label="Leading Wildcard")
	@Test
	public void leadingWildcard() {
		assertEquals(1,getDAO().getMoviesLikeTitle("%atural Disasters: Forces of Nature", 0, MAX_ROWS, null).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("%ystic River: From Page to Screen", 0, MAX_ROWS, null).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("%eventeen: The Faces for Fall", 0, MAX_ROWS, null).size());
	}

	/**
	 * The results of this and the next test demonstrate the lack of impact in using like
	 * for exact match strings.
	 */
	@TestLabel(label="Exact Like")
	@Test
	public void exactLike() {
		assertEquals(1,getDAO().getMoviesLikeTitle("Natural Disasters: Forces of Nature", 0, MAX_ROWS, null).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Mystic River: From Page to Screen", 0, MAX_ROWS, null).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Seventeen: The Faces for Fall", 0, MAX_ROWS, null).size());
	}

	/*
	 * The results of this and the previous test demonstrate the lack of impact in using like
	 * for exact match strings.
	 */
	@TestLabel(label="Exact Equals")
	@Test
	public void exactEquals() {
		assertEquals(1,getDAO().getMoviesEqualsTitle("Natural Disasters: Forces of Nature", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesEqualsTitle("Mystic River: From Page to Screen", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesEqualsTitle("Seventeen: The Faces for Fall", 0, MAX_ROWS).size());
	}
	
}