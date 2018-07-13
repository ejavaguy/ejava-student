package ejava.examples.daoex.jpa;

import static org.junit.Assert.*;



import java.util.Date;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.daoex.bo.Author;

/**
 * This class provides a scalled down version of JPAAuthorDAOTest, in that
 * it only employs an Extended Persistence Context and eliminates the
 * DAO in order to simplify the presentation of the code.
 */
public class JPAExtendedOnlyTest extends JPATestBase{
    private static final Logger log_ = LoggerFactory.getLogger(JPAExtendedOnlyTest.class);
    /**
     * This test verifies we can persist an entity.
     */
    @Test
    public void testCreate() throws Exception {
        log_.info("testCreate()");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        log_.info("creating author:" + author);

        //entity managers with extended persistence contexts can be called
        //outside of a transaction
        em.persist(author);
        log_.info("created author:" + author);        
    }
    

    /**
     * This test verifies the ability of the DAO to get an object from the 
     * database.
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception {
        log_.info("testGet()");
        Author author = new Author();
        author.setFirstName("thing");
        author.setLastName("one");
        author.setSubject("children");
        author.setPublishDate(new Date());
        
        log_.info("creating author:" + author);
        em.persist(author);
        log_.info("created author:" + author);        

        Author author2=null;
        author2 = em.find(Author.class, author.getId());
        log_.info("got author author:" + author2);

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
        log_.info("testQuery()");
        
        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Query");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        
        log_.info("creating author:" + author);
        em.persist(author);
        
        //need to associate em with Tx to allow query to see entity in DB
        try {
            em.getTransaction().begin();
            //note that the persist does not have to be within the tx
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            log_.error("",ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }

      
        Author author2 = null;
        try {
            Query query = em.createQuery(
                    "from jpaAuthor where id=" + author.getId());
            author2 = (Author)query.getSingleResult();
            log_.info("got author:" + author2);
        }
        catch (Exception ex) {
            log_.error("",ex);
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
        log_.info("testUpdate");
        
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
        
        author.setFirstName("updated " + firstName);
        author.setLastName("updated " + lastName);
        author.setSubject("updated " + subject);
        author.setPublishDate(new Date(published.getTime()+ 1000));
        try {
            em.getTransaction().begin();
            em.getTransaction().commit();
            Author dbAuthor = em.find(Author.class, author.getId());

            dbAuthor.setFirstName(author.getFirstName());
            dbAuthor.setLastName(author.getLastName());
            dbAuthor.setSubject(author.getSubject());
            dbAuthor.setPublishDate(author.getPublishDate());
            
            em.getTransaction().begin();
            em.getTransaction().commit();
            log_.info("updated author:" + author);
        }
        catch (Exception ex) {
            log_.error("",ex);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            fail("" + ex);
        }
        
        Author author2 = null;
        try {
            author2 = em.find(Author.class, author.getId());
            log_.info("got author:" + author2);
        }
        catch (Exception ex) {
            log_.error("",ex);
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
        log_.info("testMerge");
        
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
            em.flush();
            em.clear();
            em.getTransaction().commit();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.error("",ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        Author author2 = new Author(author.getId());
        author2.setFirstName("updated " + author.getFirstName());
        author2.setLastName("updated " + author.getLastName());
        author2.setSubject("updated " + author.getSubject());
        author2.setPublishDate(new Date(published.getTime()+ 1000));
        try {
            log_.info("merging with author:" + author2);
            Author tmp = em.merge(author2);
            em.getTransaction().begin();
            em.getTransaction().commit();
            log_.info("merged author:" + tmp);
        }
        catch (Exception ex) {
            log_.error("",ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        Author author3 = null;
        try {
            author3 = em.find(Author.class, author.getId());
            log_.info("got author:" + author3);
        }
        catch (Exception ex) {
            log_.error("",ex);
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
        log_.info("testRemove()");

        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Remove");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.getTransaction().commit();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.error("",ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        try {
            em.remove(author); //remove doesn't happen until tx
            em.getTransaction().begin();
            em.getTransaction().commit();
            log_.info("removed author:" + author);
        }
        catch (Exception ex) {
            log_.error("",ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }

        Author author2=null;
        try {
            author2 = em.find(Author.class, author.getId());
            log_.info("removed author:" + author);
        }
        catch (Exception ex) {
            log_.error("",ex);
            fail("" + ex);
        }
        if (author2 != null) {
            fail("object not deleted");
        }        
    }
}
