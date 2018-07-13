#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
	@Test @Ignore
	public void testUpdate() {
		log.info("*** testUpdate ***");
	}

	/**
	 * This test will demonstrate using a native SQL command within a JPA
	 * bulk update.
	 */
	@Test @Ignore
	public void testSQLUpdate() {
		log.info("*** testSQLUpdate ***");
	}
}
