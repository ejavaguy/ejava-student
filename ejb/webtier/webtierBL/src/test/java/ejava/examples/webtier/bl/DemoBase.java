package ejava.examples.webtier.bl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.StudentDAO;
import ejava.examples.webtier.jpa.StudentJPADAO;

public abstract class DemoBase {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static final String PERSISTENCE_UNIT = "webtier-test";
    protected static EntityManagerFactory emf;
    protected EntityManager em;
    protected StudentDAO dao = null;
    protected Registrar registrar;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (emf!=null) {
            emf.close();
        }
    }
    
    @Before
    public void setUp() throws Exception {
        em = emf.createEntityManager();
        dao = new StudentJPADAO();
        ((StudentJPADAO)dao).setEntityManager(em);
        registrar = new RegistrarImpl();
        ((RegistrarImpl)registrar).setStudentDAO(dao);
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        if (em!=null) {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                if (tx.getRollbackOnly() == true) { tx.rollback(); }
                else                              { tx.commit(); }
            }
        em.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        logger.info("cleaning up database");
        em.getTransaction().begin();
        List<Student> students = 
            em.createQuery("select s from Student s").getResultList();
        for(Student s: students) {
            em.remove(s);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        logger.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
