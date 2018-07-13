package ejava.jpa.hibernatemigration.legacyhbm;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ejava.jpa.hibernatemigration.BaseMigrationTest;

public class LegacyHBMTest extends BaseMigrationTest {
	private static final Logger log = LoggerFactory.getLogger(LegacyHBMTest.class);
	private static SessionFactory sessionFactory;
	private Session session;
	
	@BeforeClass
	public static void setUpClass() {
		log.debug("creating sessionFactory");
		sessionFactory=new Configuration().configure().buildSessionFactory();
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
}
