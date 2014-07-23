package ejava.examples.daoex.jpa;

import static org.junit.Assert.*;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.daoex.bo.Author;

/**
 * Provides a set of tests and demonstrations for JPA persistence context
 * membership.
 */
public class JPAMembershipTest extends JPATestBase {
    static Log log = LogFactory.getLog(JPAMembershipTest.class);

    /**
     * Demonstrates how to detach an object from the persistence context
     * so that changes to the entity are not reflected in the database.
     */
    @Test
    public void testDetach() {
        log.info("*** testDetach() ***");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        em.persist(author);

        //callers can detach entity from persistence context
        log.debug("em.contains(author)="+em.contains(author));
        log.debug("detaching author");
        em.getTransaction().begin();
        em.flush();
        em.detach(author);
        log.debug("em.contains(author)="+em.contains(author));
        em.getTransaction().commit();
        
        //changes to detached entities do not change database
        author.setFirstName("foo");
        em.getTransaction().begin();
        em.getTransaction().commit();
        Author author2 = em.find(Author.class, author.getId());
        log.debug("author.firstName=" + author.getFirstName());
        log.debug("author2.firstName=" + author2.getFirstName());
        assertFalse("unexpected name change", 
        		author.getFirstName().equals(author2.getFirstName()));
    }
    
    /** 
     * Demonstrates detach of new entity is ignored 
     */
    @Test
    public void testDetachNew() {
    	log.info("*** testDetachNew ***");
        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("new");
        
        log.debug("em.contains(author)="+em.contains(author));
        log.debug("detaching author");
        em.detach(author);
        log.debug("em.contains(author)="+em.contains(author));
     }

    /** 
     * Demonstrates detach of detached entity is ignored 
     */
    @Test
    public void testDetachDetached() {
    	log.info("*** testDetachDetached ***");
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
        log.debug("em.contains(author)="+em.contains(detached));
        log.debug("detaching detached author");
    	em.detach(detached);
        log.debug("em.contains(author)="+em.contains(detached));
     }
    
}
