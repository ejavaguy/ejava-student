package ejava.examples.webtier.jpa;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;

import ejava.examples.webtier.jpa.JPADAOTypeFactory;
import ejava.examples.webtier.jpa.JPAUtil;
import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.DAOFactory;
import ejava.examples.webtier.dao.StudentDAO;

public class JPADAOTestBase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "webtier";
    protected StudentDAO dao = null;

    @Before
    public void setUp() throws Exception {
        new JPADAOTypeFactory();
        dao = DAOFactory.getDAOTypeFactory(JPADAOTypeFactory.NAME).getStudentDAO();
        EntityManager em = JPAUtil.getEntityManager(PERSISTENCE_UNIT);
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        EntityTransaction tx = JPAUtil.getEntityManager().getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        JPAUtil.closeEntityManager();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        EntityManager em = JPAUtil.getEntityManager();
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
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
