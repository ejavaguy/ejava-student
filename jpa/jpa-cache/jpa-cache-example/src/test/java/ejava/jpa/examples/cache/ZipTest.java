package ejava.jpa.examples.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ZipTest {
	private static Log log = LogFactory.getLog(ZipTest.class);
	private static final String PU_NAME = "jpaCache";
	private static EntityManagerFactory emf;
	private EntityManager em;
	private String testZip = "01080";
	
	@BeforeClass
	public static void setUpClass() throws IOException {
		Map<String, String> props = new HashMap<String, String>();
		emf=Persistence.createEntityManagerFactory(PU_NAME, props);		
		
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		ingest(em);
		em.getTransaction().commit();
		em.clear();
		em.close();
	}
	
	@Before
	public void setUp() {
		em=emf.createEntityManager();
		em.setProperty("javax.persistence.retrieveMode ", CacheRetrieveMode.USE);
		em.setProperty("javax.persistence.cache.storeMode", CacheStoreMode.USE);
		log.info(String.format("setUp: shared cache contains(%s)=%s", 
				testZip, 
				emf.getCache().contains(ZipAddress.class, testZip)));
	}
	
	private static void ingest(EntityManager em) throws IOException {
		InputStreamReader isr = new InputStreamReader(ZipTest.class.getResourceAsStream("/zip_code_database.csv"));
		BufferedReader br = new BufferedReader(isr);
		String line=null;
		int count=0;
		while ((line=br.readLine())!=null) {
			String[] tokens=line.split(",");
			if (tokens.length < 3) { continue; } 
			String zip = tokens[0].replaceAll("\"", "");
			String city = tokens[2].replaceAll("\"", "");
			if (tokens[0].length()>=5) {
				ZipAddress za = new ZipAddress(zip, city);
				em.persist(za);
				//log.info("persisting:" + za);
			}
			if (count++ > 500) { break; }
		}
	}
	
	@After
	public void tearDown() {
		log.info(String.format("tearDown: shared cache contains(%s)=%s", 
				testZip, 
				emf.getCache().contains(ZipAddress.class, testZip)));
		if (em!=null) {
			em.close();
			em=null;
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		if (emf!=null) {
			emf.getCache().evictAll();
			emf.close();
			emf=null;
		}
	}
	
	protected void doQueries() {
		long startTime=System.currentTimeMillis();
		for (int i=0; i<3; i++) {
			ZipAddress za = em.find(ZipAddress.class, testZip);
			log.info("city for " + za.getZip() + " is " + za.getCity());
		}
		long totalTime=System.currentTimeMillis()-startTime;
		log.info("totalTime=" + totalTime + "msecs");
	}
	
	@Test
	public void test0() {
		log.info("*** test0 ***");
		doQueries();
	}
	@Test
	public void test0a() {
		log.info("*** test0 ***");
		doQueries();
	}
	
	@Test
	public void test1() {
		log.info("*** test1 - emf.getCache().evictAll() ***");
		emf.getCache().evictAll();
		doQueries();
	}
	@Test
	public void test1a() {
		log.info("*** test1 - emf.getCache().evictAll() ***");
		emf.getCache().evictAll();
		doQueries();
	}

	@Test
	public void test0b() {
		log.info("*** test0 ***");
		doQueries();
	}
	@Test
	public void test0c() {
		log.info("*** test0 ***");
		doQueries();
	}

	@Test
	public void test1b() {
		log.info("*** test1 - emf.getCache().evictAll() ***");
		emf.getCache().evictAll();
		doQueries();
	}
}
