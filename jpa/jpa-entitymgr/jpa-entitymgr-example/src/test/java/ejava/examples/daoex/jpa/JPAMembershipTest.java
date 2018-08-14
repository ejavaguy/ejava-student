package ejava.examples.daoex.jpa;

import static org.junit.Assert.*;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.daoex.bo.Author;

/**
 * Provides a set of tests and demonstrations for JPA persistence context
 * membership.
 */
public class JPAMembershipTest extends JPATestBase {
    static final Logger logger = LoggerFactory.getLogger(JPAMembershipTest.class);

    /**
     * Demonstrates how to detach an object from the persistence context
     * so that changes to the entity are not reflected in the database.
     */
    @Test
    public void testDetach() {
        logger.info("*** testDetach() ***");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        em.persist(author);

        //callers can detach entity from persistence context
        logger.debug("em.contains(author)={}", em.contains(author));
        logger.debug("detaching author");
        em.getTransaction().begin();
        em.flush();
        em.detach(author);
        logger.debug("em.contains(author)={}", em.contains(author));
        em.getTransaction().commit();
        
        //changes to detached entities do not change database
        author.setFirstName("foo");
        em.getTransaction().begin();
        em.getTransaction().commit();
        Author author2 = em.find(Author.class, author.getId());
        logger.debug("author.firstName={}", author.getFirstName());
        logger.debug("author2.firstName={}", author2.getFirstName());
        assertNotEquals("unexpected name change", author.getFirstName(), author2.getFirstName());
    }
    
    /** 
     * Demonstrates detach of new entity is ignored 
     */
    @Test
    public void testDetachNew() {
    	logger.info("*** testDetachNew ***");
        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("new");
        
        logger.debug("em.contains(author)={}", em.contains(author));
        logger.debug("detaching author");
        em.detach(author);
        logger.debug("em.contains(author)={}", em.contains(author));
     }

    /** 
     * Demonstrates detach of detached entity is ignored 
     */
    @Test
    public void testDetachDetached() {
    	logger.info("*** testDetachDetached ***");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        em.persist(author);
        em.getTransaction().begin();
        em.getTransaction().commit();

        //detaching detached entity will be ignored
        Author detached = new Author(author.getId());
        logger.debug("em.contains(author)={}", em.contains(detached));
        logger.debug("detaching detached author");
    	em.detach(detached);
        logger.debug("em.contains(author)={}", em.contains(detached));
     }
    
}
