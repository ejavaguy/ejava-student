package ejava.projects.edmv.blimpl;

import static org.junit.Assert.*;



import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.projects.edmv.blimpl.EDmvIngestor;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.dao.VehicleDAO;
import ejava.projects.edmv.jpa.JPADAOTestBase;
import ejava.projects.edmv.jpa.JPAPersonDAO;
import ejava.projects.edmv.jpa.JPAVehicleDAO;

/**
 * This class provides a basic test of the ingest capability.
 * 
 * @author jcstaff
 *
 */
public class EDmvIngestorTest extends JPADAOTestBase {
	private static Log log = LogFactory.getLog(EDmvIngestorTest.class);

	private PersonDAO personDAO;
	private VehicleDAO vehicleDAO;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		//we could easily switch to JDBC DAO versions here
	    //personDAO = new JDBCPersonDAO();
	    //((JDBCPersonDAO)personDAO).setConnection(connection);
	    //vehicleDAO = new JDBCVehicleDAO();
	    //((JDBCVehicleDAO)vehicleDAO).setConnection(connection);
		
		personDAO = new JPAPersonDAO();
		((JPAPersonDAO)personDAO).setEntityManager(em);
        vehicleDAO = new JPAVehicleDAO();
        ((JPAVehicleDAO)vehicleDAO).setEntityManager(em);
		
		em.getTransaction().begin();
	}
	
	@Test
	public void testIngestAll() throws Exception {
		log.info("*** testIngestAll ***");
		
		String fileName = "xml/dmv-all.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		EDmvIngestor ingestor = new EDmvIngestor();
		ingestor.setPersonDAO(personDAO);
		ingestor.setVehicleDAO(vehicleDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		em.getTransaction().commit();
		
		//some testing of ingest goes here...		
		assertEquals("unexpected number of people", 1000,
				em.createQuery("select count(p) from Person p", Long.class)
				.getSingleResult().intValue());		
		assertEquals("unexpected number of vehicle registrations", 1000,
				em.createQuery("select count(vr) from VehicleRegistration vr", Long.class)
				.getSingleResult().intValue());
		assertEquals("unexpected number of owners", 1500,
				em.createQuery("select count(o) from VehicleRegistration vr JOIN vr.owners as o", Long.class)
				.getSingleResult().intValue());		
		assertEquals("unexpected number of distinct owners", 772,
				em.createQuery("select count(distinct o) from VehicleRegistration vr JOIN vr.owners as o", Long.class)
				.getSingleResult().intValue());		
	}
}
