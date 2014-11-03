package ejava.projects.edmv.dao;

import java.util.List;

import ejava.projects.edmv.bo.VehicleRegistration;

/**
 * This interface provides a _sparse_ example of methods that are provided
 * by a DAO providing a mapping to the DB.
 * 
 * @author jcstaff
 *
 */
public interface VehicleDAO {
	void createRegistration(VehicleRegistration registration) 
		throws DAOException;
	List<VehicleRegistration> getRegistrations(int index, int count)
	    throws DAOException;
}
