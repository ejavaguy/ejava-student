package ejava.examples.daoex.jpa;

import static org.junit.Assert.*;


import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.daoex.bo.Author;

/**
 * Provides a set of tests and demonstrations for JPA state synchronization
 * methods.
 */
public class JPASyncTest extends JPATestBase {
    static Log log = LogFactory.getLog(JPASyncTest.class);

    /**
     * Demonstrates updating the cached state of an object once the 
     * state becomes out of sync with the database.
     */
    @Test
    public void testRefresh() {
        log.info("*** testRefresh() ***");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        em.persist(author);
        log.debug("created author:" + author);
        em.getTransaction().begin();
        em.getTransaction().commit();

        //change DB state out-of-band from the cache
        em.getTransaction().begin();
        String newName="foo";
        int count=em.createQuery(
        		"update jpaAuthor a set a.firstName=:name where a.id=:id")
        	.setParameter("id", author.getId())
        	.setParameter("name", newName)
        	.executeUpdate();
        em.getTransaction().commit();
        assertEquals("unexpected count", 1, count);
        
        //object state becomes stale when DB changed out-of-band
        log.debug("author.firstName=" + author.getFirstName());
        assertFalse("unexpected name", author.getFirstName().equals(newName));

        //get the cached object back in sync
        log.debug("calling refresh");
        em.refresh(author);
        log.debug("author.firstName=" + author.getFirstName());
        assertEquals("unexpected name", newName, author.getFirstName());
    }
    
    /** 
     * Demonstrates refresh of new entity is rejected. 
     */
    @Test
    public void testRefreshNew() {
    	log.info("*** testRefreshNew ***");
        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("new");
        
        //refreshing a new entity will get rejected
        try {
        	em.refresh(author);
        	fail("refresh of new entity not detected");
        } catch (IllegalArgumentException ex) {
        	log.debug("caught expected exception:" + ex);
        }
     }

    /** 
     * Demonstrates refresh of detached entity is rejected. 
     */
    @Test
    public void testRefreshDetached() {
    	log.info("*** testRefreshDetached ***");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        em.persist(author);
        log.debug("created author:" + author);
        em.getTransaction().begin();
        em.getTransaction().commit();

        //refreshing a detached entity will get rejected
        Author detached = new Author(author.getId());
    	em.refresh(author);
    	log.debug("refreshed managed entity");
        try {
        	em.refresh(detached);
        	fail("refresh of detached entity not detected");
        } catch (IllegalArgumentException ex) {
        	log.debug("caught expected exception:" + ex);
        }
     }
    
}
