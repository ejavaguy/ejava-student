package org.myorg.encconfig.auditor.ejb.it;

import javax.naming.Context;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.myorg.encconfig.ejb.ConfigBeanRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs an IT test of an EJB having EJB dependencies injected into it.
 */
//TODO: enc-config 29
//@Ignore
public class ConfigBeanIT {
	private static final Logger log = LoggerFactory.getLogger(ConfigBeanIT.class);
    private static final String jndiName = System.getProperty("lookup.name",
            "ejb:/encconfig-labsol-ejb/ConfigBeanEJB!" + ConfigBeanRemote.class.getName());

	private Context jndi;
	
	@Before
	public void setUp() {
		log.info("using {}", jndiName);
	}
	
	private ConfigBeanRemote getConfigBean() throws NamingException {
		if (jndi==null) {
			jndi=new InitialContext();
		}
		return (ConfigBeanRemote)jndi.lookup(jndiName);
	}
	
	@Test
	public void testNoIface() throws NamingException {
		log.info("*** testNoIface() ***");
		assertTrue("no interface EJB not injected", 
				getConfigBean().haveNoIfaceEJB());
	}

	@Test
	public void testLocalIface() throws NamingException {
		log.info("*** testLocalIface() ***");
		assertTrue("no local interface EJB injected", 
				getConfigBean().haveLocalEJB());
	}

	@Test
	public void testRemoteIface() throws NamingException {
		log.info("*** testRemoteIface() ***");
		assertTrue("no remote interface injected", 
		        getConfigBean().haveRemoteEJB());
	}
}
