package ejava.projects.edmv.blimpl;

import java.io.PrintStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import ejava.projects.edmv.bl.PersonMgmt;
import ejava.projects.edmv.bl.VehicleMgmt;
import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.jpa.JPAPersonDAO;
import ejava.projects.edmv.jpa.JPAVehicleDAO;

/**
 * This class provides an example wrapper of the business logic to show
 * how the business logic cab be wrapped in a main to be run outside
 * of JUnit.
 */
public class EDmvMgmtCommand extends EDmvIngestCommand {
	private static String command = System.getProperty("command");
	private static String indexStr = System.getProperty("index","0");
	private static String countStr = System.getProperty("count","1");
	private static PrintStream out = System.out;
	
	private static EntityManagerFactory getEMF() {
		return Persistence.createEntityManagerFactory("eDmvBO-test");
	}
	
	private static void invoke(
	        PersonMgmt personMgmt, VehicleMgmt vehicleMgmt) throws Exception {
		if (command == null) {
			throw new Exception("command not supplied");
		}
		else if ("listPeople".equals(command)) {
			listPeople(personMgmt);
		}
        else if ("listRegistrations".equals(command)) {
            listRegistrations(vehicleMgmt);
        }
		else {
			throw new Exception("unknown command:" + command);
		}
	}
	
	private static void listPeople(PersonMgmt personMgmt) 
	    throws Exception {
		
		int index = Integer.parseInt(indexStr);
		int count = Integer.parseInt(countStr);
		
		for (Person person : personMgmt.getPeople(index, count)) {
	        out.println("person:" + person);	    	
		}
	}
	
    private static void listRegistrations(VehicleMgmt vehicleMgmt) 
        throws Exception {
        
        int index = Integer.parseInt(indexStr);
        int count = Integer.parseInt(countStr);
        
        for (VehicleRegistration reg : 
            vehicleMgmt.getRegistrations(index, count)) {
            out.println("registration:" + reg);            
        }
    }

	public static void main(String args[]) {		
		try {
			EntityManagerFactory emf = null;
			EntityManager em = null;
			try {
				emf = getEMF();
				em = emf.createEntityManager();
				JPAPersonDAO personDAO = new JPAPersonDAO();
				personDAO.setEntityManager(em);
				JPAVehicleDAO vehicleDAO = new JPAVehicleDAO();
				vehicleDAO.setEntityManager(em);
				
			    EDmvMgmtImpl dmvMgmt = new EDmvMgmtImpl();
			    dmvMgmt.setPersonDAO(personDAO);
			    dmvMgmt.setVehicleDAO(vehicleDAO);
			    em.getTransaction().begin();
			    
			    invoke(dmvMgmt, dmvMgmt); //same object implements both ifaces
			}
			finally {
				if (em != null) {
					EntityTransaction tx = em.getTransaction();
					if (tx.getRollbackOnly()) { tx.rollback(); }
					else                      { tx.commit(); }
 				}
				if (emf != null) { emf.close(); }				
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}
