package ejava.examples.daoex.dao;

import javax.persistence.*;
import org.junit.*;

/**
 * This class provides a concrete BookDAO for the base class of tests.
 */
public class JPABookDAOTest extends BookDAOTestBase {
	private static final String PERSISTENCE_UNIT="book";
	private static EntityManagerFactory emf;
	private EntityManager em;
	
	@BeforeClass
	public static void setUpClass() {
		emf=Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
	}
	@Before
	public void setUp() {
		if (emf!=null) {
			em=emf.createEntityManager();
			em.getTransaction().begin();
		}
		super.dao=new JPABookDAOImpl();
		((JPABookDAOImpl)super.dao).setEntityManager(em);
	}
	@After
	public void tearDown() {
		if (em != null && em.getTransaction().isActive()) {
			if (em.getTransaction().getRollbackOnly()) {
				em.getTransaction().rollback();
			} else {
				em.getTransaction().commit();
			}
			em.close();
			em=null;
		}
	}
	@AfterClass
	public static void tearDownClass() {
		if (emf!=null) {
			emf.close();
		}
	}
}
