package org.myorg.encconfig.auditor.ejb;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.myorg.encconfig.ejb.AuditorRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the technique-independent IT tests for the configured
 * EJB. It gets extended by technique-specific IT tests 
 */
@Ignore
public class AuditorCheckerITBase {
	private static final Logger log = LoggerFactory.getLogger(AuditorCheckerITBase.class);

	private String jndiName;
	private Context jndi;
	
	protected AuditorCheckerITBase(String jndiName) {
		this.jndiName=jndiName;
	}
	
	@Before
	public void setUp() {
		log.info("using {}", jndiName);
	}
	
	@After
	public void tearDown() throws NamingException {
		if (jndi!=null){
			log.debug("closing jndi");
			jndi.close();
			log.debug("jndi closed");
		}
	}
	
	private AuditorRemote getAuditor() throws NamingException {
		if (jndi==null) {
			jndi=new InitialContext();
		}
		return (AuditorRemote)jndi.lookup(jndiName);
	}
	
	//TODO: enc-config 01: run this test
	//@Ignore
	@Test
	public void testPublishJMS() throws NamingException {
		log.info("*** testPublishJMS() ***");
		assertTrue("publishJMS value not injected", 
				getAuditor().isPublishJMS());
	}

	//TODO: enc-config 03: run this test
	@Ignore
	@Test 
	public void testPersistenceContext() throws NamingException {
		log.info("*** testPersistenceContext() ***");
		assertTrue("persistence context not injected", 
				getAuditor().havePersistenceContext());
	}

	//TODO: enc-config 05: run this test
	@Ignore
	@Test
	public void testTopic() throws NamingException {
		log.info("*** testTopic() ***");
		assertTrue("topic not injected", getAuditor().haveTopic());
	}

	//TODO: enc-config 07: run this test
	@Ignore
	@Test
	public void testConnectionFactory() throws NamingException {
		log.info("*** testConnectionFactory() ***");
		assertTrue("connection factory not injected", 
				getAuditor().haveConnectionFactory());
	}
	
	//TODO: enc-config 09: run this test
	@Ignore
	@Test
	public void testAudit() throws NamingException {
		log.info("*** testAudit() ***");
		assertEquals("EJB not properly initialized", 2, 
				getAuditor().audit("hello resources!!!"));
	}
}
