package ejava.util.jpa;


import org.apache.maven.plugin.testing.MojoRule;

import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;

import ejava.utils.jpa.JPASchemaGenMojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class GenerateMojoTest {
    @Rule
    public final MojoRule rule = new MojoRule() {	};
    
    private String readScript(String type) throws FileNotFoundException {
		Scanner scanner = null;
		try {
			File f = new File("target/test-classes/project-to-test/target/classes/ddl/jpaUtil-ittest-" + type + ".ddl");
			assertTrue("file not found: " + f.getPath(), f.exists());
			assertNotEquals("empty file created", 0L, f.length());
			scanner = new Scanner(f);
			scanner.useDelimiter("\\Z");
			String script = scanner.next();
			assertNotNull("no create script generated", script);
			assertNotEquals("empty script", 0, script.length());
			return script;
		} finally {
			if (scanner!=null) { scanner.close(); }
		}
	}

    /**
     * @throws Exception if any
     */
    @Test
    public void testGenerate() throws Exception {
        File proj = new File( "target/test-classes/project-to-test/" );
        assertTrue( proj.exists() );
        File pom = new File( "target/test-classes/project-to-test/pom.xml" );
        assertTrue( pom.exists() );
        
        File cls = new File("target/test-classes/ejava/util/jpa/Tablet.class");
        assertTrue("make sure base module is compiled, Tablet.class missing. Run mvn from command line first", cls.exists() );
        File target = new File("target/test-classes/project-to-test/target/classes/ejava/util/jpa");
        if (target.exists()) {
		  for (File f: target.listFiles()) { f.delete(); }
		  target.delete();
        }
        target.mkdirs();         
        
        //place java class compiled in base package into project-to-test/target area
        Path source = FileSystems.getDefault().getPath(cls.getPath());
        Path dest = FileSystems.getDefault().getPath(target.getPath() + "/Tablet.class");
        Files.copy(source, dest);
                
        JPASchemaGenMojo myMojo = ( JPASchemaGenMojo ) rule.lookupConfiguredMojo( proj, "generate" );
        assertNotNull( myMojo );
        
        myMojo.setPersistenceUnit("jpaUtil-ittest");
        myMojo.execute();        

		String script = readScript("create");
		assertTrue("missing create", script.contains("create table JPAUTIL_TABLET"));
		assertTrue("missing line termination", script.contains(";\n"));
		assertTrue("missing formatting", script.contains(",\n"));
		script = readScript("drop");
		assertTrue("missing drop", script.contains("drop table JPAUTIL_TABLET"));
		assertTrue("missing line termination", script.contains(";\n"));
    }
}

