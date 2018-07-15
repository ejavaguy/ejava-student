package ejava.projects.esales.blimpl;

import static org.junit.Assert.*;


import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.esales.dao.AccountDAO;
import ejava.projects.esales.bl.AccountMgmt;
import ejava.projects.esales.jpa.JPAAccountDAO;
import ejava.projects.esales.jpa.JPADAOTestBase;

public class ESalesIngestorTest extends JPADAOTestBase {
	private static Logger log = LoggerFactory.getLogger(ESalesIngestorTest.class);
	
	private AccountDAO accountDAO;
	private AccountMgmt mgmt;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		accountDAO = new JPAAccountDAO();
		((JPAAccountDAO)accountDAO).setEntityManager(em);
		mgmt = new AccountMgmtImpl();
		((AccountMgmtImpl)mgmt).setAccountDAO(accountDAO);

		em.getTransaction().begin();
	}

	@Test
	public void testIngest1() throws Exception {
		log.info("*** testIngest1 ***");
		
		String fileName = "xml/eSales-1.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",2,
				mgmt.getAccounts(0, 1000).size());
	}
	
	@Test
	public void testIngest10() throws Exception {
		log.info("*** testIngest10 ***");
		
		String fileName = "xml/eSales-10.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",19,
				mgmt.getAccounts(0, 1000).size());
	}

	@Test
	public void testIngest100() throws Exception {
		log.info("*** testIngest10 ***");
		
		String fileName = "xml/eSales-100.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",209,
				mgmt.getAccounts(0, 1000).size());
	}

	@Test
	public void testIngestAll() throws Exception {
		log.info("*** testIngestAll ***");
		
		String fileName = "xml/eSales-all.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",841,
				mgmt.getAccounts(0, 1000).size());
	}

}
