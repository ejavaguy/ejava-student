package ejava.projects.edmv.blimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.edmv.bl.EDmvException;
import ejava.projects.edmv.bl.PersonMgmt;
import ejava.projects.edmv.bl.VehicleMgmt;
import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.dao.DAOException;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.dao.VehicleDAO;

/**
 * This class provides a simple example of both the person and registration
 * management implementation. They were combined into a single implementation
 * class here for simplicity. They could easily be broken out into separate
 * classes as their implementation gets more complex.
 */
public class EDmvMgmtImpl implements PersonMgmt, VehicleMgmt {
    private Logger log = LoggerFactory.getLogger(EDmvMgmtImpl.class); 
	private PersonDAO personDAO;
	private VehicleDAO vehicleDAO;
	
	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}
	public void setVehicleDAO(VehicleDAO vehicleDAO) {
	    this.vehicleDAO = vehicleDAO;
	}	

    public List<Person> getPeople(int index, int count) throws EDmvException {
        if (count < 0) {
            throw new EDmvException("count must be >= 0");
        }
        
        try {
            return personDAO.getPeople(index, count);
        }
        catch (DAOException ex) {
            log.error("error getting people", ex);
            throw new EDmvException("error getting people:" + ex);
        }
    }
    
    public List<VehicleRegistration> getRegistrations(int index, int count)
            throws EDmvException {
        if (count < 0) {
            throw new EDmvException("count must be >= 0");
        }
        
        try {
            return vehicleDAO.getRegistrations(index, count);
        }
        catch (DAOException ex) {
            log.error("error getting registrations", ex);
            throw new EDmvException("error getting registrations:" + ex);
        }
    }
}
