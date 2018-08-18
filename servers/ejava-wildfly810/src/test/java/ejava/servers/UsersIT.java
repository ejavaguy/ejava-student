package ejava.servers;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

/**
 * This test verifies the password of the users.properties file is set correctly.
 */
public class UsersIT {
	private static final Logger log = LoggerFactory.getLogger(UsersIT.class);
	private String usersPath = "src/main/resources/standalone/configuration/application-users.properties";
	private static String password=System.getProperty("jndi.password", "password1!");
	
	@Test
	public void testCredentials() throws IOException, NamingException {
		File file = new File(usersPath);
		assertTrue("could not locate:" + file.getAbsolutePath(), file.exists());
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line=null;
		int failed=0;
		int passed=0;
		while ((line=reader.readLine())!=null) {
			if (!line.startsWith("#")) {
				String[] tokens = line.split("=");
				if (tokens.length==2) {
					String user=tokens[0];
					Properties env = new Properties();
					env.put(Context.SECURITY_PRINCIPAL, user);
					env.put(Context.SECURITY_CREDENTIALS, password);
					InitialContext jndi = null;
					try {
						jndi=new InitialContext(env);
						jndi.lookup("/jms");
						//System.out.println(user + "/" + password + " passed");
						log.debug(user + " passed");
						passed += 1;
					} catch (NamingException ex) {
						System.err.println(user + " failed:" + ex);
						log.error(user + " failed:" + ex);
						failed+=1;
					} finally {
						if (jndi!=null) { jndi.close(); }
					}
				}
			}
		}
		reader.close();
		assertTrue("nothing passed", passed > 0);
		assertEquals("unexpected passwords", 0, failed);
 	}

}
