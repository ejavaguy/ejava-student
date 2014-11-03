package ejava.projects.edmv.bl;

import java.util.List;

import ejava.projects.edmv.bo.VehicleRegistration;

/**
 * This interface provides a _sparse_ example of business logic that 
 * provides management of vehicle registrations.
 * 
 * @author jcstaff
 *
 */
public interface VehicleMgmt {
    List<VehicleRegistration> getRegistrations(int index, int count)
        throws EDmvException;
}
