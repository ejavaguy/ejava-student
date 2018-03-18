package ejava.examples.daoex.jpa;

import static org.junit.Assert.*;


import java.util.Date;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.daoex.bo.Author;

/**
 * This class provides an exploded view of the DAO operations for clarify
 * of the Java Persistence API, but at the risk of obscuring the use of a 
 * DAO. DAOs are still useful and necessary when using JPA. This unit test
 * is just an early demo of the mechanics of the API, NOT the architecture 
 * that should surround the use of the API.
 */
public class JPACRUDTest extends JPATestBase {
    static Log log = LogFactory.getLog(JPACRUDTest.class);
    /**
     * This test verifies we can persist an entity.
     */
    @Test
    public void testCreate() {
        log.info("*** testCreate() ***");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        log.debug("creating author:" + author);
        assertFalse("unexpected initialized id", author.getId() > 0);

        log.debug("em.contains(author)=" + em.contains(author));
        //entity managers with extended persistence contexts can be called
        //outside of a transaction
        em.persist(author);
        log.debug("created author:" + author);        
        log.debug("em.contains(author)=" + em.contains(author));
        assertTrue("missing id", author.getId() > 0);
    }
    
    /**
     * Demonstrates existing entity persist is ignored
     * @throws Exception
     */
    @Test
    public void testCreateExisting() {
    	log.info("*** testCreateExisting ***");
    	
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        log.debug("creating author first time:" + author);

        log.debug("em.contains(author)=" + em.contains(author));
        em.persist(author);
        log.debug("created author:" + author);        
        log.debug("em.contains(author)=" + em.contains(author));

        //entity managers will ignore persists for existing entity
        em.persist(author);
    }
    

    /**
     * Demonstrates how a detached entity will get rejected from persist
     * @throws Exception
     */
    @Test
    public void testCreateDetached() throws Exception {
        log.info("*** testCreateDetached() ***");
        Author author = new Author(1);
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        log.debug("creating author:" + author);

        log.debug("em.contains(author)=" + em.contains(author));
        //entity managers will reject a detached entity
        try {
        	em.persist(author);
        	fail("did not detect detached entity");
        } catch (PersistenceException ex) {
        	log.debug("caught expected exception:" + ex);
        }
        log.debug("em.contains(author)=" + em.contains(author));
    }

    /**
     * This test verifies the ability of the DAO to get an object from the 
     * database.
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception {
        log.info("*** testGet() ***");
        Author author = new Author();
        author.setFirstName("thing");
        author.setLastName("one");
        author.setSubject("children");
        author.setPublishDate(new Date());
        
        log.debug("creating author:" + author);
        em.persist(author);
        log.debug("created author:" + author);        

        Author author2=null;
        author2 = em.find(Author.class, author.getId());
        log.debug("got author author:" + author2);

        assertEquals(author.getFirstName(), author2.getFirstName());
        assertEquals(author.getLastName(), author2.getLastName());
        assertEquals(author.getSubject(), author2.getSubject());
        assertEquals(author.getPublishDate(), author2.getPublishDate());
    }

    /**
     * This test verifies the functionality of a query method that simply 
     * queries by the primary key value.
     */
    @Test
    public void testQuery() throws Exception {
        log.info("*** testQuery() ***");
        
        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Query");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        
        log.debug("creating author:" + author);
        em.persist(author);
        
        //need to associate em with Tx to allow query to see entity in DB
        try {
            em.getTransaction().begin();
            //note that the persist does not have to be within the tx
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            log.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }

      
        Author author2 = null;
        try {
            Query query = em.createQuery(
                    "from jpaAuthor where id=" + author.getId());
            author2 = (Author)query.getSingleResult();
            log.debug("got author:" + author2);
        }
        catch (Exception ex) {
            log.fatal(ex);
            fail("" + ex);
        }
        
        assertNotNull(author2);
        assertEquals(author.getFirstName(), author2.getFirstName());
        assertEquals(author.getLastName(), author2.getLastName());
        assertEquals(author.getSubject(), author2.getSubject());
        assertEquals(author.getPublishDate(), author2.getPublishDate());
    }
    

    /**
     * This tests the ability to update on object.
     */
    @Test
    public void testUpdate() throws Exception {
        log.info("*** testUpdate");
        
        String firstName="test";
        String lastName="Update";
        String subject="testing";
        Date published = new Date();
        
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setSubject(subject);
        author.setPublishDate(published);        
        em.persist(author);                
        assertTrue("author is not managed", em.contains(author));
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        //make sure we have our author in the DB but not currently 
        //managed by the entity manager
        em.clear();
        assertFalse("author is still managed", em.contains(author));        
        
        //now make some changes to our baseline object
        author.setFirstName("updated " + firstName);
        author.setLastName("updated " + lastName);
        author.setSubject("updated " + subject);
        author.setPublishDate(new Date(published.getTime()+ 1000));
        
        //show that the DB has not been updated
        Author dbAuthor = em.find(Author.class,author.getId());
        assertFalse("unexpected first name", 
                author.getFirstName().equals(dbAuthor.getFirstName()));
        assertFalse("unexpected last name", 
                author.getLastName().equals(dbAuthor.getLastName()));
        assertFalse("unexpected subject", 
                author.getSubject().equals(dbAuthor.getSubject()));
        assertFalse("unexpected publish date", 
                author.getPublishDate().equals(dbAuthor.getPublishDate()));        
        
        try {
            //example of using setter methods to make changes in DB
            dbAuthor = em.find(Author.class, author.getId());

            dbAuthor.setFirstName(author.getFirstName());
            dbAuthor.setLastName(author.getLastName());
            dbAuthor.setSubject(author.getSubject());
            dbAuthor.setPublishDate(author.getPublishDate());
            
            em.getTransaction().begin();
            em.getTransaction().commit();
            log.debug("updated author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            fail("" + ex);
        }
        
        //now verify changes were made to DB
        Author author2 = null;
        try {
            author2 = em.find(Author.class, author.getId());
            log.debug("got author:" + author2);
        }
        catch (Exception ex) {
            log.fatal(ex);
            fail("" + ex);
        }
        
        assertNotNull(author2);
        assertEquals("updated " + firstName, author2.getFirstName());
        assertEquals("updated " + lastName, author2.getLastName());
        assertEquals("updated " + subject, author2.getSubject());
        assertEquals(new Date(published.getTime()+1000), author2.getPublishDate());
    }

    /**
     * This tests the ability to merge an object.
     */
    @Test
    public void testMerge() throws Exception {
        log.info("*** testMerge");
        
        String firstName="test";
        String lastName="Merge";
        String subject="testing";
        Date published = new Date();
        
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setSubject(subject);
        author.setPublishDate(published);        
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.getTransaction().commit();
            log.debug("created author:" + author);
        }
        catch (Exception ex) {
            fail("unexpected error during persist" + ex);
        }
        
        //create a new object with the same primary key as the one in the DB
        Author author2 = new Author(author.getId());
        author2.setFirstName("updated " + author.getFirstName());
        author2.setLastName("updated " + author.getLastName());
        author2.setSubject("updated " + author.getSubject());
        author2.setPublishDate(new Date(published.getTime()+ 1000));
        try {
            log.debug("merging with author:" + author2);
            Author tmp = em.merge(author2);
            em.getTransaction().begin();
            em.getTransaction().commit();
            log.debug("merged author:" + tmp);
            assertFalse("author2 is managed", em.contains(author2));
            assertTrue("tmp Author is not managed", em.contains(tmp));
            assertSame("merged result not existing managed", author, tmp);
        }
        catch (Exception ex) {
            fail("unexpected error during merge" + ex);
        }
        
        //verify our changes were made to the DB
        Author author3 = em.find(Author.class, author.getId());
        log.debug("got author:" + author3);
        
        assertNotNull(author3);
        assertEquals("updated " + firstName, author3.getFirstName());
        assertEquals("updated " + lastName, author3.getLastName());
        assertEquals("updated " + subject, author3.getSubject());
        assertEquals(new Date(published.getTime()+1000), author3.getPublishDate());
    }
    
    @Test
    public void testRemove() throws Exception {
        log.info("*** testRemove() ***");

        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Remove");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.getTransaction().commit();
            log.debug("created author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        try {
            em.remove(author); //remove doesn't happen until tx
            em.getTransaction().begin();
            em.getTransaction().commit();
            log.debug("removed author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }

        Author author2=null;
        try {
            author2 = em.find(Author.class, author.getId());
            log.debug("removed author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            fail("" + ex);
        }
        if (author2 != null) {
            fail("object not deleted");
        }        
    }
    
    
    /**
     * Demonstrates that removing a new entity is ignored.
     * @throws Exception
     */
    @Test
    public void testRemoveNew() throws Exception {
        log.info("*** testRemoveNew() ***");

        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("New");
        author.setSubject("testing");
        author.setPublishDate(new Date());

        //entity managers will ignore removal of new entity
        log.debug("em.contains(author)=" + em.contains(author));
        em.remove(author);
        log.debug("em.contains(author)=" + em.contains(author));
    }

    /**
     * Demonstrates that removing a new entity is rejected
     * @throws Exception
     */
    @Test
    public void testRemoveDetached() throws Exception {
        log.info("*** testRemoveDetached() ***");

        Author author = new Author(1);
        author.setFirstName("test");
        author.setLastName("Detached");
        author.setSubject("testing");
        author.setPublishDate(new Date());

        try {
        	//entity managers will reject the removal of detached entity
        	em.remove(author);
        	fail("did not reject removal of detached object");
        } catch (IllegalArgumentException ex) {
        	log.debug("caught expected exception:" + ex);
        }
    }
    
    /**
     * Demonstrates that removing a removed entity is ignored.
     * @throws Exception
     */
    @Test
    public void testRemoveRemoved() throws Exception {
        log.info("*** testRemoveRemoved() ***");

        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("New");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        em.persist(author);
        log.debug("peristed:" + author);
        
        log.debug("em.contains(author)=" + em.contains(author));
        em.remove(author);
        log.debug("em.contains(author)=" + em.contains(author));
        
        //entity managers will ignore the removal of a removed entity
        em.remove(author);
        log.debug("em.contains(author)=" + em.contains(author));
    }

}
