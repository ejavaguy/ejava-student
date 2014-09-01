package ejava.projects.edmv.dao;

import java.util.List;
import ejava.projects.edmv.bo.DMVVehicleRegistration;

/**
 * This interface provides core functionality needed by the DataGen to 
 * access vehicle registrations.
 * 
 * @author jcstaff
 *
 */
public interface DMVVehicleDAO {
    DMVVehicleRegistration getRegistration(long id);
    List<DMVVehicleRegistration> getRegistrations(int index, int count);
}
