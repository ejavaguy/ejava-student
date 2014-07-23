package ejava.examples.daoex.jpa;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.daoex.AuthorDAO;
import ejava.examples.daoex.bo.Author;
import ejava.examples.daoex.jpa.JPAAuthorDAO;

/**
 * This class provides demo of using a DAO implemented with JPA
 * 
 * @author jcstaff
 */
public class JPAAuthorDAOTest extends JPATestBase {
    private static Log log = LogFactory.getLog(JPAAuthorDAOTest.class);
    private AuthorDAO dao;
        
    @Override
    public void setUp() throws Exception {
    	super.setUp();
        dao = new JPAAuthorDAO();
        ((JPAAuthorDAO)dao).setEntityManager(em);
    }
    
    /**
     * This test verifies we can persist an entity.
     */
    @Test
    public void testCreate() throws Exception {
        log.info("testCreate()");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        log.info("creating author:" + author);

        //entity managers with extended persistence contexts can be called
        //outside of a transaction
        dao.create(author);
        log.info("created author:" + author);        
    }
    

    /**
     * This test verifies the ability of the DAO to get an object from the 
     * database.
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception {
        log.info("testGet()");
        Author author = new Author();
        author.setFirstName("thing");
        author.setLastName("one");
        author.setSubject("children");
        author.setPublishDate(new Date());
        
        log.info("creating author:" + author);
        dao.create(author);
        log.info("created author:" + author);        

        Author author2=null;
        author2 = dao.get(author.getId());
        log.info("got author author:" + author2);

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
        log.info("testQuery()");
        
        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Query");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        
        log.info("creating author:" + author);
        dao.create(author);
        
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
            author2 = dao.getByQuery(author.getId());
            log.info("got author:" + author2);
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
        log.info("testUpdate");
        
        String firstName="test";
        String lastName="Update";
        String subject="testing";
        Date published = new Date();
        
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setSubject(subject);
        author.setPublishDate(published);        
        dao.create(author);        
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
        Author dbAuthor = dao.get(author.getId());
        assertFalse("unexpected first name", 
                author.getFirstName().equals(dbAuthor.getFirstName()));
        assertFalse("unexpected last name", 
                author.getLastName().equals(dbAuthor.getLastName()));
        assertFalse("unexpected subject", 
                author.getFirstName().equals(dbAuthor.getSubject()));
        assertFalse("unexpected publish date", 
                author.getFirstName().equals(dbAuthor.getPublishDate()));

        try {
            //example of using update to make changes in DB
            dao.update(author);
            em.getTransaction().begin();
            em.getTransaction().commit();

            log.info("updated author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            fail("" + ex);
        }

        //verify changes were made to DB
        Author author2 = null;
        try {
            author2 = dao.get(author.getId());
            log.info("got author:" + author2);
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
        log.info("testMerge");
        
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
            dao.create(author);
            em.flush();
            em.getTransaction().commit();
            em.clear();
            log.info("created author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        //create a new object with the same primary key as the one in the DB
        Author author2 = new Author(author.getId());
        author2.setFirstName("updated " + author.getFirstName());
        author2.setLastName("updated " + author.getLastName());
        author2.setSubject("updated " + author.getSubject());
        author2.setPublishDate(new Date(published.getTime()+ 1000));
        try {
            log.info("merging with author:" + author2);
            Author tmp = dao.updateByMerge(author2);
            em.getTransaction().begin();
            em.getTransaction().commit();
            log.info("merged author:" + tmp);
            
            assertFalse("author2 is managed", em.contains(author2));
            assertTrue("tmp Author is not managed", em.contains(tmp));
        }
        catch (Exception ex) {
            log.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        //verify our changes were made to the DB
        Author author3 = null;
        try {
            author3 = dao.get(author.getId());
            log.info("got author:" + author3);
        }
        catch (Exception ex) {
            log.fatal(ex);
            fail("" + ex);
        }
        
        assertNotNull(author3);
        assertEquals("updated " + firstName, author3.getFirstName());
        assertEquals("updated " + lastName, author3.getLastName());
        assertEquals("updated " + subject, author3.getSubject());
        assertEquals(new Date(published.getTime()+1000), author3.getPublishDate());
    }
    
    @Test
    public void testRemove() throws Exception {
        log.info("testRemove()");

        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Remove");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        try {
            em.getTransaction().begin();
            dao.create(author);
            em.getTransaction().commit();
            log.info("created author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        try {
            dao.remove(author); //remove doesn't happen until tx
            em.getTransaction().begin();
            em.getTransaction().commit();
            log.info("removed author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }

        Author author2=null;
        try {
            author2 = dao.get(author.getId());
            log.info("removed author:" + author);
        }
        catch (Exception ex) {
            log.fatal(ex);
            fail("" + ex);
        }
        if (author2 != null) {
            fail("object not deleted");
        }        
    }

}
