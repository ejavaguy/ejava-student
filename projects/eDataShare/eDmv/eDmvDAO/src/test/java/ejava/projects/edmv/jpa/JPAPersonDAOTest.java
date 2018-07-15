package ejava.projects.edmv.jpa;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.dao.PersonDAO;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 * 
 * @author jcstaff
 *
 */
public class JPAPersonDAOTest extends JPADAOTestBase {
	private static Logger log = LoggerFactory.getLogger(JPAPersonDAOTest.class);
	
	PersonDAO dao;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	    dao = new JPAPersonDAO();
	    ((JPAPersonDAO)dao).setEntityManager(em);
		em.getTransaction().begin();
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dao=null;
	}

	
	/**
	 * This method tests a single create into the database using the DAO. 
	 * This tests some core functionality, but clearly more types of inserts
	 * should also be tested. For example, what happens when the same userId 
	 * is added a second time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJPACreate() throws Exception {
		log.info("*** testJPACreate ***");
		
    	Person person = new Person();
    	person.setLastName("foobar");
    	log.debug("created person:" + person);
    	
    	dao.createPerson(person);
    	log.debug("persisted person:" + person);
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JPA resources.
    	
    	em.getTransaction().commit();
    	assertTrue(em.contains(person));
    	
    		//the entity will persisted but no longer be cached after em.clear()
    	em.clear();
    	assertFalse(em.contains(person));
    	
    	
    	Person person2 = em.find(Person.class, person.getId());
      	log.debug("checking person...");

    	assertNotNull(person2);
    	assertEquals("unexpected name", 
    	        person.getLastName(), 
    	        person2.getLastName());
    	
      	log.debug("JPA person looked good...");
	}
}
