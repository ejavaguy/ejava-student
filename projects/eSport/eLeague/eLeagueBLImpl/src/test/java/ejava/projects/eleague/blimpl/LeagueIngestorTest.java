package ejava.projects.eleague.blimpl;

import static org.junit.Assert.*;


import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.eleague.blimpl.LeagueIngestor;
import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.jpa.JPAClubDAO;
import ejava.projects.eleague.jpa.JPADAOTestBase;

/**
 * This class provides a test of the business logic classes in the league.
 *
 */
public class LeagueIngestorTest extends JPADAOTestBase {
	private static Logger logger = LoggerFactory.getLogger(LeagueIngestorTest.class);
	private ClubDAO clubDAO;

	@Override
	public void setUp() throws Exception {
		super.setUp();
	    clubDAO = new JPAClubDAO();
	    ((JPAClubDAO)clubDAO).setEntityManager(em);
		
		em.getTransaction().begin();
	}

	@Test
	public void testIngestAll() throws Exception {
		logger.info("*** testIngestAll ***");
		
		String fileName = "xml/eLeague-all.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		LeagueIngestor ingestor = new LeagueIngestor();
		ingestor.setClubDAO(clubDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		assertEquals("unexpected number of addresses", 18,
			((Long)em.createQuery("select count(a) from Address a").getSingleResult()).intValue());
		assertEquals("unexpected number of venues", 18,
				((Long)em.createQuery("select count(v) from Venue v").getSingleResult()).intValue());
	}

}
