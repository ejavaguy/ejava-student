package ejava.examples.daoex.jpa;

import java.util.Date;
import java.util.Random;

import javax.persistence.TypedQuery;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.daoex.bo.Author;

/**
 * Provides a set of tests and demonstrations for JPA 2.2 features.
 */
public class JPA22Test extends JPATestBase {
    private static final Logger logger = LoggerFactory.getLogger(JPA22Test.class);
    private Random random = new Random();

    private Author makeAuthor() {
        Author a = new Author();
        a.setFirstName("" + random.nextInt(1000));
        a.setLastName("" + random.nextInt(1000));
        a.setPublishDate(new Date());
        a.setSubject("" + random.nextInt(1000));
        return a;
    }
    
    @Test
    public void streamResult() {
        for (int i=0; i<100; i++) {
            em.persist(makeAuthor());
        }
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        TypedQuery<Author> query = em.createQuery(
                "select a from jpaAuthor a", 
                Author.class);
        
        query.getResultStream()
             .forEach(a->logger.info("{}",a));
    }
}
