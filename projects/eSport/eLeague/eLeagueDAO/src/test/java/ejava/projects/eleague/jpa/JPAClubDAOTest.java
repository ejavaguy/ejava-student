package ejava.projects.eleague.jpa;

import static org.junit.Assert.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.eleague.bo.Address;
import ejava.projects.eleague.bo.Venue;
import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.jpa.JPAClubDAO;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 * 
 */
public class JPAClubDAOTest extends JPADAOTestBase {
	private static Logger log = LoggerFactory.getLogger(JPAClubDAO.class);
	//this code assumes all the JDBC properties were placed in 
	//META-INF/persistence.xml when the file was copied from src to the 
	//target tree
	
	private ClubDAO dao;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	    dao = new JPAClubDAO();
	    ((JPAClubDAO)dao).setEntityManager(em);
		em.getTransaction().begin();
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
		
    	String name = "myVenue";
    	
    	Venue venue = new Venue();
    	venue.setName(name);
    	venue.setAddress(new Address(0, "Laurel"));
    	
    	log.debug("instantiated Venue:" + venue);
    	
    	dao.createVenue(venue);
    	log.debug("dao created Venue:" + venue);
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JPA resources.
    	
    	em.flush();
    	assertTrue(em.contains(venue));
    	em.clear();
    	em.getTransaction().commit();
    	assertFalse(em.contains(venue));
    	
    	Venue venue2 = em.find(Venue.class, venue.getId());
      	log.debug("checking venue...");

    	assertNotNull(venue2);
    	assertEquals("unexpected name", name, venue2.getName());
    	assertNotNull("missing address", venue.getAddress());
    	assertEquals("unexpected city", 
    	        venue.getAddress().getCity(),
    	        venue2.getAddress().getCity());
    	
      	log.debug("JPA venue looked good...");
	}
}
