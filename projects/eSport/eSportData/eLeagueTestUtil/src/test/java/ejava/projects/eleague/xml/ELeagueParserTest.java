package ejava.projects.eleague.xml;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import ejava.projects.eleague.dto.Contest;
import ejava.projects.eleague.dto.Division;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.Season;

/**
 * This class provides a test of the provided XML data file and DTO parer.
 * @author jcstaff
 *
 */
public class ELeagueParserTest {
	private static final Log log = LogFactory.getLog(ELeagueParserTest.class);
	private String inputDir = System.getProperty("inputDir", "target/classes/xml");
	
	@Before
	public void setUp() {
		assertNotNull("inputDir not supplied", inputDir);
	}
	
	/**
	 * This test will parse each of the XML present in the resource
	 * directory.
	 * @throws Exception
	 */
	@Test
	public void testParser() throws Exception {
		File inDir = new File(inputDir);
		File[] files = inDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith("eLeague-") &&
						name.endsWith(".xml"));
			}
		});
		for (File file : files) {
			testParser(file.getCanonicalPath());
		}
	}
	private void testParser(String inputFile) throws Exception {
		log.info("*** testParser:" + inputFile + " ***");
		
		InputStream is = new FileInputStream(inputFile);
		BufferedInputStream bis = new BufferedInputStream(is);
		ELeagueParser parser = new ELeagueParser(ELeague.class, bis);
		Object object=null;
		do {
	        object = parser.getObject(
	                    "contact", "league-metadata", "club", "season");
			if (object != null) { 
				log.debug( dump(object) );
				if (object instanceof Season) {
					checkSeason((Season) object);
				}
			};			
		} while (object != null);
		bis.close();
	}

	int level = 0;
	static final int MAX_LEVELS = 25;
	
	private String dump(Object object) throws Exception {	    
	    level += 1;
		StringBuilder text = new StringBuilder();
		if (object == null) {
		    text.append("null");
		}
		else if (object instanceof Collection && level <= MAX_LEVELS) {
		    text.append("{");
		    for(@SuppressWarnings({ "rawtypes", "unchecked" })
			Iterator<Object> itr=((Collection)object).iterator();
		        itr.hasNext();) {
		        dump(itr.next());
		        if (itr.hasNext()) {
		            text.append(",");
		        }
		    }
            text.append("}");
		}
        else if (object.getClass().getName().startsWith("java.lang")) {
            if (object instanceof Contest) { log.debug("3");}
            text.append(object);    
        }
        else if (object.getClass().getName().startsWith("java.util.Date")) {
            if (object instanceof Contest) { log.debug("4");}

            text.append(object);    
        }
		else if (level <= MAX_LEVELS){		
    		for (Method m : object.getClass().getDeclaredMethods()) {
    			if (m.getName().startsWith("get")) {
    				String propertyName = m.getName().substring("get".length());
    				try {
        				Object value = m.invoke(object, new Object[] {});
        				text.append(object.getClass().getName() + "." +
        						propertyName + "=" +
        						dump(value) + "\n");
    				} catch (IllegalArgumentException notAGetter) {
                        text.append(object.getClass().getName() + "." +
                                propertyName + "=???\n");
    				} catch (IllegalAccessException notAGetter) {
                        text.append(object.getClass().getName() + "." +
                                propertyName + "=???\n");
                    }
    			}
    		}
		}
		level -= 1;
		return text.toString();
	}
	
	private void checkSeason(Season season) {
		for (Division division : season.getDivision()) {
			checkDivision(season, division);
		}
	}
	
	private void checkDivision(Season season, Division division) {
		if ("Spring NeverEnds".equals(season.getName())) {
	        assertNotNull("division contact was null", division.getContact());
		}
	}
}
