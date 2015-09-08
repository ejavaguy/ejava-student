package ejava.projects.eleague.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.dao.ClubDAOException;
import ejava.projects.eleague.bo.Address;
import ejava.projects.eleague.bo.Venue;

/**
 * This class implements data access to the club portion of the league
 * using JDBC.
 * @author jcstaff
 *
 */
public class JDBCClubDAO implements ClubDAO {
	private static Log log = LogFactory.getLog(JDBCClubDAO.class);
	private Connection connection;
	
	/**
	 * This method injects a connection to be used by all DAO methods.
	 * @param connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void createVenue(Venue venue) {
		PreparedStatement statement1 = null;
		Statement statement2 = null;
		PreparedStatement statement3 = null;
		Statement statement4 = null;
		ResultSet rs = null;
		
		Address address = venue.getAddress();
		if (address == null) {
		    throw new ClubDAOException("Venue must have address");
		}
		
		try {
            statement1 = connection.prepareStatement(
                    "INSERT INTO ELEAGUE_ADDR (ID, CITY) " +
                    "VALUES (null, ?)");
            statement1.setString(1, venue.getAddress().getCity());
		    statement1.execute();
		    
            Method setId = Address.class.getDeclaredMethod(
	                    "setId", new Class[]{long.class});
            setId.setAccessible(true);
                //this is HSQL-specific
                //gets its db-generated primary key
            statement2 = connection.createStatement();
            rs = statement2.executeQuery("call identity()");
            if (rs.next()) {
                long id = rs.getLong(1);
                setId.invoke(venue.getAddress(), id);
            }
            rs.close();
		    
			statement3 = connection.prepareStatement(
                "INSERT INTO ELEAGUE_VEN (ID, NAME, ADDR_ID) " +
                "VALUES (null, ?, ?)");
			statement3.setString(1, venue.getName());
			statement3.setLong(2, venue.getAddress().getId());
			statement3.execute();
						
            setId = Venue.class.getDeclaredMethod(
                    "setId", new Class[]{long.class});
            setId.setAccessible(true);
                //this is HSQL-specific
                //gets its db-generated primary key
            statement4 = connection.createStatement();
            rs = statement4.executeQuery("call identity()");
            if (rs.next()) {
                long id = rs.getLong(1);
                setId.invoke(venue, id);
            }
		}
		catch (SQLException ex) {
		    log.error("SQL error creating account", ex);
		    throw new ClubDAOException("error creating account"+ex, ex);
		}
		catch (Exception ex) {
		    log.error("error creating account", ex);
		    throw new ClubDAOException("error creating account"+ex, ex);
		}
		finally {
			try { rs.close(); } catch (Exception ignored) {}
			try { statement1.close(); } catch (Exception ignored) {}
			try { statement2.close(); } catch (Exception ignored) {}
			try { statement3.close(); } catch (Exception ignored) {}
			try { statement4.close(); } catch (Exception ignored) {}
		}
	}

	/**
	 * This method is left un-implemented,
	 */
	@Override
	public List<Venue> getVenues(int index, int count)
			throws ClubDAOException {
		throw new ClubDAOException("not implemented", null);
	}
}
