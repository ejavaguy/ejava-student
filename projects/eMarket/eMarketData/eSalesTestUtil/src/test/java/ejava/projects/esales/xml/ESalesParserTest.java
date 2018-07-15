package ejava.projects.esales.xml;

import ejava.projects.esales.dto.Account;

import ejava.projects.esales.dto.ESales;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ESalesParserTest {
	private static final Logger log = LoggerFactory.getLogger(ESalesParser.class);
	private String inputDir = System.getProperty("inputDir");
	
	@Before
	public void setUp() {
		assertNotNull("inputDir not supplied", inputDir);
	}
	
	@Test
	public void testParser() throws Exception {
		File inDir = new File(inputDir);
		File[] files = inDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith("eSales-") &&
						name.endsWith(".xml"));
			}
		});
		for (File file : files) {
			testParser(file.getCanonicalPath());
		}
	}
	
	public void testParser(String inputFile) throws Exception {
		log.info("*** testParser:" + inputFile + " ***");
		
		InputStream is = new FileInputStream(inputFile);
		BufferedInputStream bis = new BufferedInputStream(is);
		ESalesParser parser = new ESalesParser(ESales.class, bis);
		Object object=null;
		do {
			object = parser.getObject("Address", "Account", "Auction", "Bid","Image");
			if (object != null) { dump(object); };
			if (object instanceof Account) {
				checkAccount((Account)object);
			}
		} while (object != null);
		bis.close();
	}

	private void checkAccount(Account account) {
		assertNotNull("null login:" + account.getRefid(), account.getLogin());	
		assertTrue("account.addresses was empty:" + account.getRefid(), account.getAddress().size() > 0);
	}

	private void dump(Object object) throws Exception {
		StringBuilder text = new StringBuilder();
		for (Method m : object.getClass().getDeclaredMethods()) {
			if (m.getName().startsWith("get")) {
				String propertyName =m.getName().substring("get".length());
				Object value = m.invoke(object, new Object[] {});
				text.append(object.getClass().getName() + "." +
						propertyName + "=" +
						value + "\n");
			}
		}
		log.debug("{}",text);
	}
}
