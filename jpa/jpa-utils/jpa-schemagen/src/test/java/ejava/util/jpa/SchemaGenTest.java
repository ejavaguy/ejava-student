package ejava.util.jpa;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaGenTest {
	private static final Logger logger = LoggerFactory.getLogger(SchemaGenTest.class);
	
	private String readScript(String type) throws FileNotFoundException {
		Scanner scanner = null;
		try {
			File f = new File("target/classes/ddl/jpaUtil-" + type + ".ddl");
			scanner = new Scanner(f);
			scanner.useDelimiter("\\Z");
			String script = scanner.next();
			logger.info("{}", script);
			assertNotNull("no create script generated", script);
			assertNotEquals("empty script", 0, script.length());
			return script;
		} finally {
			if (scanner!=null) { scanner.close(); }
		}		
	}
	
	@Test
	public void createScript() throws FileNotFoundException {
		String script = readScript("create");
		assertTrue("missing create", script.contains("create table JPAUTIL_TABLET"));
	}
	
	@Test
	public void dropScript() throws FileNotFoundException {
		String script = readScript("drop");
		assertTrue("missing create", script.contains("drop table JPAUTIL_TABLET"));
	}	
}
