package ejava.projects.esales.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.esales.bo.Account;
import ejava.projects.esales.bo.Address;
import ejava.projects.esales.dao.AccountDAO;
import ejava.projects.esales.dao.AccountDAOException;

public class JDBCAccountDAO implements AccountDAO {
	private static Logger log = LoggerFactory.getLogger(JDBCAccountDAO.class);
	private Connection connection;
	
	public void setConnection(Connection connection) {
            this.connection = connection;
	}

	public void createAccount(Account account) {
            PreparedStatement statement1 = null;
            PreparedStatement statement2 = null;
            Statement statement3 = null;
            PreparedStatement statement4 = null;
            ResultSet rs = null;
            try {
                statement1 = connection.prepareStatement(
                    "INSERT INTO ESALES_ACCT " +
                    "(USER_ID, FIRST_NAME) " +
                    "VALUES (?, ?)");
                statement1.setString(1, account.getUserId());
                statement1.setString(2, account.getFirstName());
                statement1.execute();
                                        
                statement2 = connection.prepareStatement(
                "INSERT INTO ESALES_ADDRESS " +
                "(ID, NAME, CITY) " +
                "VALUES (null, ?, ?)");
                statement3 = connection.createStatement(); 
                statement4 = connection.prepareStatement(
                "INSERT INTO ESALES_ACCT_ADDRESS_LINK " +
                "(USER_ID, ADDRESS_ID) " +
                "VALUES (?, ?)");
                
                Method setId = Address.class.getDeclaredMethod(
                                "setId", new Class[]{long.class});
                setId.setAccessible(true);
                for (Address address : account.getAddresses()) {
                    //insert the address
                    statement2.setString(1, address.getName());
                    statement2.setString(2, address.getCity());
                    statement2.execute();
                    
                    //this is HSQL-specific
                    //gets its db-generated primary key
                    rs = statement3.executeQuery("call identity()");
                    rs.next();
                    long id = rs.getLong(1);
                    setId.invoke(address, id);
                    
                    statement4.setString(1, account.getUserId());
                    statement4.setLong(2, address.getId());
                    statement4.execute();
                }
            }
            catch (SQLException ex) {
                log.error("SQL error creating account", ex);
                throw new AccountDAOException("error creating account"+ex, ex);
            }
            catch (Exception ex) {
                log.error("error creating account", ex);
                throw new AccountDAOException("error creating account"+ex, ex);
            }
            finally {
                    try { rs.close(); } catch (Exception ignored) {}
                    try { statement1.close(); } catch (Exception ignored) {}
                    try { statement2.close(); } catch (Exception ignored) {}
                    try { statement3.close(); } catch (Exception ignored) {}
                    try { statement4.close(); } catch (Exception ignored) {}
            }
	}

	public List<Account> getAccounts(int index, int count)
			throws AccountDAOException {
		throw new AccountDAOException("not implemented", null);
	}
}
