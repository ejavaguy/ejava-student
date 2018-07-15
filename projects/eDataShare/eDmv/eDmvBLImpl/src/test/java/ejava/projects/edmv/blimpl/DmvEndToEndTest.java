package ejava.projects.edmv.blimpl;

import static org.junit.Assert.*;


import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.edmv.bl.PersonMgmt;
import ejava.projects.edmv.bl.VehicleMgmt;
import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.dao.VehicleDAO;
import ejava.projects.edmv.jpa.JPADAOTestBase;
import ejava.projects.edmv.jpa.JPAPersonDAO;
import ejava.projects.edmv.jpa.JPAVehicleDAO;

/**
 * This class provides an end-to-end demonstration of the DMV business logic.
 */
public class DmvEndToEndTest extends JPADAOTestBase {
	private static Logger log = LoggerFactory.getLogger(DmvEndToEndTest.class);
	protected PersonDAO personDAO;
	protected VehicleDAO vrDAO;
	protected PersonMgmt personMgmt;
	protected VehicleMgmt vehicleMgmt;

	/**
	 * The DMV application shall be able to list the persons and vehicle
	 * registrations. 
	 * @throws Exception 
	 */
	@Test
	public void testEndToEnd() throws Exception {
		log.info("*** testEndToEnd ***");
		
		//the admin shall be able to coldstart the application
		super.cleanup();
		assertEquals("unexpected people", 0, getCount(Person.class.getName()));
		assertEquals("unexpected registrations", 0, getCount(VehicleRegistration.class.getName()));
		
		//the admin shall be able to ingest a starting point for the application
		EDmvIngestor ingestor = getIngestor();
		em.getTransaction().begin();
		ingestor.ingest();
		em.getTransaction().commit();
		assertEquals("unexpected people", 1000, getCount(Person.class.getName()));
		assertEquals("unexpected registrations", 1000, getCount(VehicleRegistration.class.getName()));
		
		//the user shall be able to get a list of people in the application
		int count=0;
		for (Person p: em.createQuery("select p from Person p", Person.class).getResultList()) {
			log.debug(p);
			count += 1;
		}
		assertEquals("unexpected number of people listed", 1000, count);
		
		//the user shall be able to get a list of vehicle registrations in the application
		count=0;
		for (VehicleRegistration vr: 
			em.createQuery("select vr from VehicleRegistration vr", VehicleRegistration.class)
				.getResultList()) {
			log.debug(vr);
			count += 1;
		}
		assertEquals("unexpected number of vehicles listed", 1000, count);
	}
	
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		personDAO = new JPAPersonDAO();
		((JPAPersonDAO)personDAO).setEntityManager(em);
		vrDAO = new JPAVehicleDAO();
		((JPAVehicleDAO)vrDAO).setEntityManager(em);
		EDmvMgmtImpl dmvImpl = new EDmvMgmtImpl();
		personMgmt = dmvImpl;
		vehicleMgmt = dmvImpl;
		dmvImpl.setPersonDAO(personDAO);
		dmvImpl.setVehicleDAO(vrDAO);
	}
	
	protected int getCount(String entityName) {
		return em.createQuery(
				String.format("select count(e) from %s e", entityName), Long.class)
			.getSingleResult().intValue();
	}

	protected EDmvIngestor getIngestor() {
		String fileName = "xml/dmv-all.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		EDmvIngestor ingestor = new EDmvIngestor();
		ingestor.setPersonDAO(personDAO);
		ingestor.setVehicleDAO(vrDAO);
		ingestor.setInputStream(is);
		return ingestor;
	}
}
