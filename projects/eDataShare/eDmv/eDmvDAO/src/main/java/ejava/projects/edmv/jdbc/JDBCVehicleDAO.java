package ejava.projects.edmv.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.dao.DAOException;
import ejava.projects.edmv.dao.VehicleDAO;

/**
 * This class provides a _sparse_ example of a JDBC DAO that implements 
 * O/R mapping for objects related to vehicles.
 *
 */
public class JDBCVehicleDAO implements VehicleDAO {
	private static Logger log = LoggerFactory.getLogger(JDBCVehicleDAO.class);
	private Connection connection;
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void createRegistration(VehicleRegistration registration) 
	    throws DAOException {
		PreparedStatement statement1 = null;
		Statement statement2 = null;
		PreparedStatement statement3 = null;
				
		try {
            statement1 = connection.prepareStatement(
                    "INSERT INTO EDMV_VREG " +
                    "(ID, VIN) " +
                    "VALUES (null, ?)");
            statement1.setString(1, registration.getVin());
		    statement1.execute();
		 
			ResultSet rs = null;
		    try {
                Method setId = VehicleRegistration.class.getDeclaredMethod(
    	                    "setId", new Class[]{long.class});
                setId.setAccessible(true);
                    //this is HSQL-specific
                    //gets its db-generated primary key
                statement2 = connection.createStatement();
                rs = statement2.executeQuery("call identity()");
                if (rs.next()) {
                    long id = rs.getLong(1);
                    setId.invoke(registration, id);
                }
		    }
		    catch (Exception ex) {
	            log.error("SQL error getting registration id:" + ex, ex);
	            throw new DAOException("error getting registration id:"+ex, ex);
		    } finally {
				try { rs.close(); } catch (Exception ignored) {}
		    }
            
            for (Person owner : registration.getOwners()) {
                if (owner.getId() == 0) {
                    throw new DAOException("transient Vehicle Owner found");
                }
                statement3 = connection.prepareStatement(
                        "INSERT INTO EDMV_VREG_OWNER_LINK " +
                        "(VEHICLE_ID, OWNER_ID) " +
                        "VALUES (?, ?)");
                    statement3.setLong(1, registration.getId());
                    statement3.setLong(2, owner.getId());
                    statement3.execute();
            }
		}
		catch (SQLException ex) {
		    log.error("SQL error creating registration:" + ex, ex);
		    throw new DAOException("error creating registration:"+ex, ex);
		}
		finally {
			try { statement1.close(); } catch (Exception ignored) {}
			try { statement2.close(); } catch (Exception ignored) {}
			try { statement3.close(); } catch (Exception ignored) {}
		}
	}

	public List<VehicleRegistration> getRegistrations(int index, int count)
			throws DAOException {
		throw new DAOException("not implemented");
	}
}
