package ejava.projects.eleague.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;

/**
 * This base class handles the common tasks associated with setting up and 
 * tearing down the connection to the JDBC database.
 */
public class JDBCDAOTestBase {
    private static final Log log = LogFactory.getLog(JDBCDAOTestBase.class);

    private static String jdbcDriver = System.getProperty("jdbc.driver", "org.h2.Driver");
    private static String jdbcURL = System.getProperty("jdbc.url", "jdbc:h2:./target/h2db/ejava");
    private static String jdbcUser = System.getProperty("jdbc.user", "sa");
    private static String jdbcPassword = System.getProperty("jdbc.password", "");
    protected Connection connection;

    @Before
    public void setUp() throws Exception {		
        log.debug("loading JDBC driver:" + jdbcDriver);
        Thread.currentThread()
              .getContextClassLoader()
              .loadClass(jdbcDriver)
              .newInstance();
        
        log.debug(String.format("getting connection: %s, %s/%s", 
                        jdbcURL, jdbcUser, jdbcPassword));
        connection = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
        
        connection.setAutoCommit(false);
        cleanup();
    }

    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.commit();
            connection.close();
        }
    }

    private void cleanup() throws Exception {
        Statement statement=null;
        try {
            statement = connection.createStatement();
			statement.execute("DELETE FROM ELEAGUE_VEN");
			statement.execute("DELETE FROM ELEAGUE_ADDR");
        }
        finally {
        	if (statement != null) { statement.close(); }			
        }
    }
}
