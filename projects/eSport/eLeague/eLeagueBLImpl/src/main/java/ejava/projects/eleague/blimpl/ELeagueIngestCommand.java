package ejava.projects.eleague.blimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.eleague.jdbc.JDBCClubDAO;

public class ELeagueIngestCommand {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ELeagueIngestCommand.class);
	private static final String jdbcDriver = 
		System.getProperty("jdbc.driver");
	private static final String jdbcURL = 
		System.getProperty("jdbc.url");
	private static final String jdbcUser = 
		System.getProperty("jdbc.user");
	private static final String jdbcPassword = 
		System.getProperty("jdbc.password");
	private static final String inputFile = 
		System.getProperty("inputFile");
	
	@SuppressWarnings("resource") //"is" is closed in main()
	private static InputStream getInputStream() throws Exception {
		InputStream is = null;

		if (inputFile == null) {
			throw new Exception("inputFile not supplied");
		}
		
		File file = new File(inputFile);
		if (file.exists()) {
			is = new FileInputStream(file);
		}
		if (is == null) {
            is = Thread.currentThread()
                       .getContextClassLoader()
                       .getResourceAsStream(inputFile);
		}
		if (is == null) {
			throw new Exception("unable to locate inputFile:" + inputFile);
		}
		return is;
	}
	
	private static void loadDriver() throws Exception {
		if (jdbcDriver == null) {
			throw new Exception("jdbc.driver not supplied");
		}
		Thread.currentThread()
		      .getContextClassLoader()
		      .loadClass(jdbcDriver)
		      .newInstance();
	}
	
	private static Connection getConnection() throws Exception {
		if (jdbcURL == null) {
			throw new Exception("jdbc.url not supplied");
		}
		if (jdbcUser == null) {
			throw new Exception("jdbc.user not supplied");
		}
		if (jdbcPassword == null) {
			throw new Exception("jdbc.password not supplied");
		}
		return DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
	}
	
	public static void main(String args[]) {
	    try {
			Connection connection = null;
			InputStream is = null;
		    try {
		    	is = getInputStream();
		    	loadDriver();
		    	connection = getConnection();
		    	JDBCClubDAO accountDAO = new JDBCClubDAO();
		    	 accountDAO.setConnection(connection);
		    	
		    	ELeagueIngestor ingest = new ELeagueIngestor();
		    	ingest.setClubDAO(accountDAO);
		    	ingest.setInputStream(is);
		    	ingest.ingest();
		    }
			finally {
				if (connection != null) { connection.close(); }
				if (is != null) { is.close(); }
			}
	    }
	    catch (Exception ex) {
	    	ex.printStackTrace();
	    	System.exit(-1);
	    }
	}
}
