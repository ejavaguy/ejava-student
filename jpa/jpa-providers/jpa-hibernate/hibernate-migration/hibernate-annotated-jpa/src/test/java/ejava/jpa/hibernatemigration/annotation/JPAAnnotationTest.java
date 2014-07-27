package ejava.jpa.hibernatemigration.annotation;

import java.io.Serializable;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ejava.jpa.hibernatemigration.BaseAnnotatedMigrationTest;

public class JPAAnnotationTest extends BaseAnnotatedMigrationTest {
	private static final Log log = LogFactory.getLog(JPAAnnotationTest.class);
	private static final String PERSISTENCE_UNIT_NAME = "hibernate-migration-sales";
	private static EntityManagerFactory emf;
	private EntityManager em;
	
	@BeforeClass
	public static void setUpClass() {
		log.debug("creating entityManagerFactory");
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}
	
	@Before
	public void setUp() {
		log.debug("creating session");
		em = emf.createEntityManager();
		em.getTransaction().begin();
	}
	
	@After
	public void tearDown() {
		if (em != null) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
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
	
	@Override
	protected void save(Object entity) { em.persist(entity); }
	@Override
	protected void flush() { em.flush(); }
	@Override
	protected void clear() { em.clear(); }
	@Override
	protected <T> T get(Class<T> clazz, Serializable pk) { return em.find(clazz, pk); }
	@Override
	protected void beginTransaction() { 
		if (em!=null) { em.getTransaction().begin(); }
	}
	@Override
	protected void commitTransaction() {
		if (em!=null) { em.getTransaction().commit(); } 
	}
}
