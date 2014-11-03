package ejava.projects.edmv.jpa;

import static org.junit.Assert.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.dao.VehicleDAO;
import ejava.projects.edmv.jpa.JPAPersonDAO;
import ejava.projects.edmv.jpa.JPAVehicleDAO;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 * 
 * @author jcstaff
 *
 */
public class JPAVehicleDAOTest extends JPADAOTestBase {
	private static Log log = LogFactory.getLog(JPAVehicleDAOTest.class);

	private VehicleDAO vehicleDAO;
	private PersonDAO personDAO;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	    vehicleDAO = new JPAVehicleDAO();
	    ((JPAVehicleDAO)vehicleDAO).setEntityManager(em);
	    personDAO = new JPAPersonDAO();
	    ((JPAPersonDAO)personDAO).setEntityManager(em);
		
		em.getTransaction().begin();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		vehicleDAO=null;
		personDAO=null;
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
		
        Person owner1 = new Person();
        owner1.setLastName("Brown");
        personDAO.createPerson(owner1);
        log.debug("created owner1:" + owner1);
        
        Person owner2 = new Person();
        owner2.setLastName("Harper");
        personDAO.createPerson(owner2);
        log.debug("created owner2:" + owner2);

        VehicleRegistration registration = new VehicleRegistration();
        registration.setVin("456");
        registration.getOwners().add(owner1);
        registration.getOwners().add(owner2);
        vehicleDAO.createRegistration(registration);
        log.debug("created registration:" + registration);
            	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JPA resources.
    	
    	em.getTransaction().commit();
    	assertTrue(em.contains(registration));
    	
    		//the entity will persisted but no longer be cached after em.clear()
    	em.clear();
    	assertFalse(em.contains(registration));
    	
    	VehicleRegistration registration2 = 
    	    em.find(VehicleRegistration.class, registration.getId());
      	log.debug("checking registration...");

    	assertNotNull(registration2);
    	assertEquals("unexpected vin", 
    	        registration.getVin(), 
    	        registration2.getVin());
    	assertEquals("unexpected owners", 
    	        2, registration2.getOwners().size());
    	for (Person owner : registration.getOwners()) {
    	    assertTrue("unexpected owner name:" + owner.getLastName(), 
    	            owner1.getLastName().equals(owner.getLastName()) ||
    	            owner2.getLastName().equals(owner.getLastName()));
    	}
    	
      	log.debug("JPA registration and owners looked good...");
	}
}
