package ejava.jpa.hibernatemigration.annotated;

import static org.junit.Assert.*;

import java.io.Serializable;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.SQLGrammarException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.jpa.hibernatemigration.BaseAnnotatedMigrationTest;
import ejava.jpa.hibernatemigration.CustomizedConfiguration;

public class CustomizedConfigurationHBMTest extends BaseAnnotatedMigrationTest {
	private static final Log log = LogFactory.getLog(CustomizedConfigurationHBMTest.class);
	private static SessionFactory sessionFactory;
	private Session session;
	
	@BeforeClass
	public static void setUpClass() {
		log.debug("creating sessionFactory");
		sessionFactory=new CustomizedConfiguration().configure().buildSessionFactory();
	}
	
	@Before
	public void setUp() {
		log.debug("creating session");
		session = sessionFactory.getCurrentSession();
		session.beginTransaction();
	}
	
	@After
	public void tearDown() {
		if (session != null) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		if (sessionFactory!=null) {
			sessionFactory.close();
		}
	}
	
	@Override
	protected void save(Object entity) { session.save(entity); }
	@Override
	protected void flush() { session.flush(); }
	@Override
	protected void clear() { session.clear(); }
	@Override
	@SuppressWarnings("unchecked")
	protected <T> T get(Class<T> clazz, Serializable pk) { return (T)session.get(clazz, pk); }
	@Override
	protected void beginTransaction() { sessionFactory.getCurrentSession().beginTransaction(); }
	@Override
	protected void commitTransaction() { sessionFactory.getCurrentSession().getTransaction().commit(); }
	
	/**
	 * This test verifies the schema in the database was overridden with the tableName provided in 
	 * hibernate.xfg.xml 
	 */
	@Test
	public void verifyOverride() {
		log.info("*** verifyOverride ***");
		
		//this should throw an exception if the table did not exist
		session.createSQLQuery("select * from HMIG_CLERK_OVERRIDE").list();
		
		//verify a bad table name would have thrown an exception
		try {
			session.createSQLQuery("select * from HMIG_BOGUS").list();
			fail("found unexpected table HMIG_BOGUS");
		} catch (SQLGrammarException expected) {
			log.debug("received expected exception:" + expected);
		}
	}
}
