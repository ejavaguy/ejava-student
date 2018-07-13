#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Ignore;
import org.junit.Test;

public class QueryTest extends QueryBase {
	private static final Logger log = LoggerFactory.getLogger(QueryTest.class);
	
	/**
	 * This test method demonstrates retrieving zero to many matching entities
	 * from a database.
	 */
	@Test @Ignore
	public void testMulti() {
		log.info("*** testMulti ***");
	}

	/**
	 * This test method demonstrates retrieving a single result from the 
	 * database when there is a single row that matches.
	 */
	@Test @Ignore
	public void testSingle() {
		log.info("*** testSingle ***");
	}

	/**
	 * This test method demonstrates the exception that is thrown when 
	 * no matching rows exist in the database when asking for a single result.
	 */
	@Test(expected=NoResultException.class) @Ignore
	public void testSingleNoResult() {
		log.info("*** testSingleNoResult ***");
	}

	/**
	 * This test method demonstrates the exception that is thrown when 
	 * multiple matching rows exist in the database when asking for a single result.
	 */
	@Test(expected=NonUniqueResultException.class) @Ignore
	public void testSingleNonUniqueResult() {
		log.info("*** testSingleNonUniqueResult ***");
	}

	/**
	 * This test method demonstrates the ability to pass in parameters to a 
	 * query.
	 */
	@Test @Ignore
	public void testParameters() {
		log.info("*** testParameters ***");
	}

	/**
	 * This test method demonstrates the ability to control the number of results 
	 * returned from a query and to page through those results.
	 */
	@Test @Ignore
	public void testPaging() {
		log.info("*** testPaging ***");
	}
	
	/**
	 * This test method demonstrates passing a query hint to the provider for  
	 * the query execution.
	 */
	@Test @Ignore
	public void testNamedQuery() {
		log.info("*** testNamedQuery ***");
	}
	
	/**
	 * This test method demonstrates retrieving values from a query and 
	 * not the complete managed entity.
	 */
	@Test @Ignore
	public void testValueQuery() {
		log.info("*** testValueQuery ***");
	}
	
	/**
	 * This test method demonstrates retrieving the value result of a query 
	 * function
	 */
	@Test @Ignore
	public void testResultValueQuery() {
		log.info("*** testResultValueQuery ***");
	}
	
	/**
	 * This test method demonstrates retrieving multiple values from a query.
	 */
	@Test @Ignore
	public void testMultiValueQuery() {
		log.info("*** testMultiValueQuery ***");
	}
	
    /**
     * This test method demonstrates the ability to create a result class to 
     * encapsulate the results returned from a value query.
     */
    @Test @Ignore
	public void testResultClass() {
		log.info("*** testResultClass ***");
	}
	
}
