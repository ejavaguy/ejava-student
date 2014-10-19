package org.myorg.encconfig.auditor.ejb;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.myorg.encconfig.ejb.AuditorRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the technique-independent IT tests for the configured
 * EJB. It gets extended by technique-specific IT tests 
 */
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
	
	private AuditorRemote getAuditor() throws NamingException {
		if (jndi==null) {
			jndi=new InitialContext();
		}
		return (AuditorRemote)jndi.lookup(jndiName);
	}
	
	@Test
	public void testPublishJMS() throws NamingException {
		log.info("*** testPublishJMS() ***");
		assertTrue("publishJMS value not injected", 
				getAuditor().isPublishJMS());
	}

	@Test
	public void testPersistenceContext() throws NamingException {
		log.info("*** testPersistenceContext() ***");
		assertTrue("persistence context not injected", 
				getAuditor().havePersistenceContext());
	}

	@Test
	public void testTopic() throws NamingException {
		log.info("*** testTopic() ***");
		assertTrue("topic not injected", getAuditor().haveTopic());
	}

	@Test
	public void testConnectionFactory() throws NamingException {
		log.info("*** testConnectionFactory() ***");
		assertTrue("connection factory not injected", 
				getAuditor().haveConnectionFactory());
	}
	
	@Test
	public void testAudit() throws NamingException {
		log.info("*** testAudit() ***");
		assertEquals("EJB not properly initialized", 2, 
				getAuditor().audit("hello resources!!!"));
	}
}
