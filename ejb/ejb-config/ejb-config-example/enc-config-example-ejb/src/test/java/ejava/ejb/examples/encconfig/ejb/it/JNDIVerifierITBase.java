package ejava.ejb.examples.encconfig.ejb.it;

import static org.junit.Assert.*;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ejb.examples.encconfig.ejb.JNDIReaderRemote;

@Ignore
public class JNDIVerifierITBase  {
	private static final Logger log = LoggerFactory.getLogger(JNDIVerifierITBase.class);
	private String ejbJNDI;
	private Context jndi;
	
	public JNDIVerifierITBase(String ejbJNDI) {
		this.ejbJNDI = ejbJNDI;
	}
			
	private JNDIReaderRemote getReader() throws NamingException {
		if (jndi==null) {
			jndi=new InitialContext();
		}
		return (JNDIReaderRemote)jndi.lookup(ejbJNDI);
	}

	protected void verify(JNDIReaderRemote reader, String name) {
		log.info("ejb={}", ejbJNDI);
		String jndiName = "java:comp/env/" + name;
		Object object = reader.lookupJNDI(jndiName);
		log.info("{}={}", jndiName, object);
		assertNotNull(jndiName + " not found", object);

		object = reader.lookupSessionContext(name);
		log.info("{}={}", name, object);
		assertNotNull(name + " not found", object);
	}
	
	@BeforeClass
	public static void setUpClass() {
	    try { Thread.sleep(1000); } catch (Exception ex) {}
	}

	@Test
	public void testValue2() throws NamingException {
		log.info("*** testValue2 ***");
		String name="val/value2";
		verify(getReader(), name);
	}

	@Test
	public void testValue3() throws NamingException {
		log.info("*** testValue3 ***");
		String name="val/value3";
		verify(getReader(), name);
	}

	@Test
	public void testDS2() throws NamingException {
		log.info("*** testDS2 ***");
		String name="jdbc/ds2";
		verify(getReader(), name);
	}

	@Test
	public void testDS3() throws NamingException {
		log.info("*** testDS3 ***");
		String name="jdbc/ds3";
		verify(getReader(), name);
	}

	@Test
	public void testQueue2() throws NamingException {
		log.info("*** testQueue2 ***");
		String name="jms/queue2";
		verify(getReader(), name);
	}

	@Test
	public void testQueue3() throws NamingException {
		log.info("*** testQueue3 ***");
		String name="jms/queue3";
		verify(getReader(), name);
	}

	@Test
	public void testTopic2() throws NamingException {
		log.info("*** testTopic2 ***");
		String name="jms/topic2";
		verify(getReader(), name);
	}

	@Test
	public void testTopic3() throws NamingException {
		log.info("*** testTopic3 ***");
		String name="jms/topic3";
		verify(getReader(), name);
	}

	@Test
	public void testCF2() throws NamingException {
		log.info("*** testCF2 ***");
		String name="jms/cf2";
		verify(getReader(), name);
	}

	@Test
	public void testCF3() throws NamingException {
		log.info("*** testCF3 ***");
		String name="jms/cf3";
		verify(getReader(), name);
	}

	@Test
	public void testEJB2() throws NamingException {
		log.info("*** testEJB2 ***");
		String name="ejb/ejb2";
		verify(getReader(), name);
	}

	@Test
	public void testEM2() throws NamingException {
		log.info("*** testEM2 ***");
		String name="jpa/em2";
		verify(getReader(), name);
	}

	@Test
	public void testEM3() throws NamingException {
		log.info("*** testEM3 ***");
		String name="jpa/em3";
		verify(getReader(), name);
	}

	@Test
	public void testEMF2() throws NamingException {
		log.info("*** testEMF2 ***");
		String name="jpa/emf2";
		verify(getReader(), name);
	}

	@Test
	public void testEMF3() throws NamingException {
		log.info("*** testEMF3 ***");
		String name="jpa/emf3";
		verify(getReader(), name);
	}
}
