package ejava.projects.edmv.jdbc;

import static org.junit.Assert.*;


import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.dao.VehicleDAO;
import ejava.projects.edmv.jdbc.JDBCPersonDAO;
import ejava.projects.edmv.jdbc.JDBCVehicleDAO;

/**
 * This test case provides an example of one might test the JDBC DAO. It 
 * provides most of the basics, knowing the DAO only implemented one method.
 * As more tests are added, the implementation show here would benefit from
 * re-usable techniques to check the values within the database.
 * 
 */
public class JDBCVehicleDAOTest extends JDBCDAOTestBase {
	private static Log log = LogFactory.getLog(JDBCVehicleDAOTest.class);
	private VehicleDAO vehicleDAO;
	private PersonDAO personDAO;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
	    vehicleDAO = new JDBCVehicleDAO();
	    ((JDBCVehicleDAO)vehicleDAO).setConnection(connection);
	    personDAO = new JDBCPersonDAO();
	    ((JDBCPersonDAO)personDAO).setConnection(connection);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	    ((JDBCVehicleDAO)vehicleDAO).setConnection(null);
	    ((JDBCPersonDAO)personDAO).setConnection(null);
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
	public void testJDBCCreate() throws Exception {
		log.info("*** testJDBCCreate ***");

		Person owner1 = new Person();
		owner1.setLastName("Smith");
		personDAO.createPerson(owner1);
		log.debug("created owner1:" + owner1);
		
        Person owner2 = new Person();
        owner2.setLastName("Jones");
        personDAO.createPerson(owner2);
        log.debug("created owner2:" + owner2);

        VehicleRegistration registration = new VehicleRegistration();
        registration.setVin("123");
        registration.getOwners().add(owner1);
        registration.getOwners().add(owner2);
        vehicleDAO.createRegistration(registration);
        log.debug("created registration:" + registration);
        
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JDBC resources.
    	
    	connection.commit();
    	Statement statement = null;
    	ResultSet rs = null;
    	try {
    	    statement = connection.createStatement();
    	    
        	log.debug("getting core vehicle info...");
    	    rs = statement.executeQuery(
    	    		"SELECT VIN FROM EDMV_VREG WHERE ID='" 
    	    		+ registration.getId() + "'");
    	    if (rs.next()) {
    	    	String vin = rs.getString("VIN");
    	    	assertEquals("unexpected vin", registration.getVin(), vin);
    	    }
    	    else {
    	    	fail("no registration found");
    	    }
            log.debug("JDBC registration looks okay");
    	        	    
    	    rs = statement.executeQuery(
    	    	"SELECT LAST_NAME " +
    	    	"FROM EDMV_PERSON, EDMV_VREG, EDMV_VREG_OWNER_LINK " +
    	    	"WHERE EDMV_PERSON.ID = EDMV_VREG_OWNER_LINK.OWNER_ID " +
    	    	    "AND EDMV_VREG_OWNER_LINK.VEHICLE_ID = " + 
    	    	    registration.getId());
    	    while (rs.next()) {
    	    	String name = rs.getString("LAST_NAME");
    	    	assertTrue("unexpected last name", 
    	    	        owner1.getLastName().equals(name) ||
    	    	        owner2.getLastName().equals(name));
    	    }
    	    
        	log.debug("JDBC owners looks good");
    	}
    	finally {
    		if (rs != null) { rs.close(); }
    		if (statement != null) { statement.close(); }
    	}
	}
}
