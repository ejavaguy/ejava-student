package ejava.examples.webtier.jpa;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.StudentDAO;

public class JPADAOTestBase {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final String PERSISTENCE_UNIT = "webtier-test";
    private static EntityManagerFactory emf;
    private EntityManager em;
    protected StudentDAO dao = null;

    @BeforeClass
    public static void setupClass() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        em = emf.createEntityManager();
        dao = new StudentJPADAO();
        ((StudentJPADAO)dao).setEntityManager(em);
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        em.getTransaction().begin();
        List<Student> students = 
            em.createQuery("select s from Student s").getResultList();
        for(Student s: students) {
            em.remove(s);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
