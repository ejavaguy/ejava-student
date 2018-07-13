package ejava.jpa.hibernatemigration.legacyhbm;

import java.io.Serializable;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ejava.jpa.hibernatemigration.BaseMigrationTest;

public class JPASessionTest extends BaseMigrationTest {
	private static final Logger log = LoggerFactory.getLogger(JPASessionTest.class);
	private static final String PERSISTENCE_UNIT_NAME = "hibernate-migration-sales";
	private static EntityManagerFactory emf;
	private EntityManager em;
	
	@BeforeClass
	public static void setUpClass() {
		log.debug("creating EMF");
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}
	
	@Before
	public void setUp() {
		log.debug("creating session/EntityManager");
		em = emf.createEntityManager();
		beginTransaction();
	}
	
	@After
	public void tearDown() {
		if (em != null) {
			if (getCurrentSession().getTransaction().isActive()) {
				commitTransaction();
			}
			em.close();
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		if (emf!=null) {
			emf.close();
		}
	}
	
	protected Session getCurrentSession() { return (em==null)?null : (Session)em.getDelegate(); }
	@Override
	protected void save(Object entity) { getCurrentSession().save(entity); }
	@Override
	protected void flush() { getCurrentSession().flush(); }
	@Override
	protected void clear() { getCurrentSession().clear(); }
	@Override
	@SuppressWarnings("unchecked")
	protected <T> T get(Class<T> clazz, Serializable pk) { return (T)getCurrentSession().get(clazz, pk); }
	@Override
	protected void beginTransaction() { getCurrentSession().beginTransaction(); }
	@Override
	protected void commitTransaction() { getCurrentSession().getTransaction().commit(); }
}
