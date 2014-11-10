package info.ejava.examples.securepingclient.ejbclient;


import static org.junit.Assert.*;

import info.ejava.examples.secureping.ejb.SecurePingClient;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.util.ejb.EJBClient;

/**
 * This class peforms a check of the SecurePingClientEJB using RMI Calls.
 * The SecurePingClientEJB lets anyone in and then performs the same
 * action on the SecurePingEJB using a run-as admin1 identity and admin role.
 * 
 * This specific class uses the JNDI InitialContext class to authenticate the
 * current user with the server.
 */
public class SecurePingClientRemotingIT extends SecurePingClientTestBase {
    static final Logger logger = LoggerFactory.getLogger(SecurePingClientRemotingIT.class);
    protected String jndiName = System.getProperty("jndi.name.secureping",
            EJBClient.getRemoteLookupName("securePingClientEAR", "securePingClientEJB", 
                "SecurePingClientEJB", 
                info.ejava.examples.secureping.ejb.SecurePingClientRemote.class.getName()));
    
    /**
     * This method will add the caller credentials to the credentials 
     * supplied in the jndi.properties file. If there are default credentials
     * already there -- these should take precendence.
     * @param username
     * @return
     * @throws NamingException
     */
    protected Context runAs(String username) throws NamingException {
    	Properties env = new Properties();
    	if (username != null) {
    		env.put(Context.SECURITY_PRINCIPAL, username);
    		env.put(Context.SECURITY_CREDENTIALS, "password1!");
    	}
        env.put("jboss.naming.client.ejb.context", true); //override anything we put there for EJBClient
    	Context context = new InitialContext(env);
    	return context;
    }

    /**
     * This method walks through each login and checks whether they are in 
     * expected roles. Inspect the log to see the value SecurePingClientEJB
     * thinks we are in and then what SecurePingEJB things after doing a
     * run-as.
     */
    @Test
    public void testIsCallerInRole() throws Exception {
        logger.info("*** testIsCallerInRole ***");
        Context jndi=null;
        
        try {
        	jndi=runAs(null);        	
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
        	assertFalse("anonomous in admin role", ejb.isCallerInRole("admin"));
            assertFalse("anonomous in user role", ejb.isCallerInRole("user"));
            assertFalse("anonomous in internalRole role", ejb.isCallerInRole("internalRole"));
        } catch (Exception ex) {
            logger.info("caught exception for anonymous caller:" +ex);
        }
        
    	jndi=runAs(knownUser);
    	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
        assertFalse("known in admin role", ejb.isCallerInRole("admin"));
        assertFalse("known in user role", ejb.isCallerInRole("user"));
        assertFalse("known in internalRole role", ejb.isCallerInRole("internalRole"));
        
    	jndi = runAs(userUser);
    	ejb=(SecurePingClient)jndi.lookup(jndiName);
        assertFalse("user in admin role", ejb.isCallerInRole("admin"));
        assertTrue("user not in user role", ejb.isCallerInRole("user"));
        assertFalse("user in internalRole role", ejb.isCallerInRole("internalRole"));

        jndi = runAs(adminUser);
    	ejb=(SecurePingClient)jndi.lookup(jndiName);
        assertTrue("admin not in admin role", ejb.isCallerInRole("admin"));
        assertTrue("admin not in user role", ejb.isCallerInRole("user"));
        //securePingClientEJB does not have this role mapped to admin
        assertFalse("admin not in internalRole role", ejb.isCallerInRole("internalRole"));
    }

    /**
     * This will invoke the SecurePingClientEJB using several different
     * logins. The SecurePingClientEJB will then pass the call off to 
     * SecurePingEJB. Both will report security information to the log as 
     * to what the container thought of the caller.
     */
    @Test
    public void testPingAll() throws Exception {
        logger.info("*** testPingAll ***");
        Context jndi=null;
        
       try {
        	jndi=runAs(null);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingAll());
        }
        catch (Exception ex) {
            logger.info("caught expected exception for anonymous:" + ex);
        }

        try {
        	jndi = runAs(knownUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
        	String result=ejb.pingAll();
            logger.info(result);
            String expected = String.format(
"securePingClient called pingAll, principal=%s, isUser=false, isAdmin=false, isInternalRole=false:\n"+
"securePing=called pingAll, principal=admin1, isUser=false, isAdmin=true, isInternalRole=true", knownUser);
            assertEquals("", expected, result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }
        
        try {
        	jndi = runAs(userUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
        	String result=ejb.pingAll();
            logger.info(result);
            String expected = String.format(
"securePingClient called pingAll, principal=%s, isUser=true, isAdmin=false, isInternalRole=false:\n"+
"securePing=called pingAll, principal=admin1, isUser=false, isAdmin=true, isInternalRole=true", userUser);
            assertEquals("", expected, result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        

        try {
        	jndi = runAs(adminUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
        	String result=ejb.pingAll();
            logger.info(result);
            String expected = String.format(
"securePingClient called pingAll, principal=%s, isUser=true, isAdmin=true, isInternalRole=false:\n"+
"securePing=called pingAll, principal=admin1, isUser=false, isAdmin=true, isInternalRole=true", adminUser);
            assertEquals("", expected, result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        
    }
    
    /**
     * This will invoke the SecurePingClientEJB using several different
     * logins. The SecurePingClientEJB will then pass the call off to 
     * SecurePingEJB. Both will report security information to the log as 
     * to what the container thought of the caller.
     */
    @Test
    public void testPingAdmin() throws Exception {    	
        logger.info("*** testPingAdmin ***");
        Context jndi=null;

        try {
        	jndi=runAs(null);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            fail("unexpected exception for anonymous:" + ex);
            logger.info("caught expected exception for anonymous:" + ex);
        }

        try {
        	jndi = runAs(adminUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        
        try {
        	jndi = runAs(knownUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        
        try {
        	jndi = runAs(userUser);        	
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }        
    }

    /**
     * This will invoke the SecurePingClientEJB using several different
     * logins. The SecurePingClientEJB will then pass the call off to 
     * SecurePingEJB. Both will report security information to the log as 
     * to what the container thought of the caller.
     */
    @Test
    public void testPingExcluded() throws Exception {
        logger.info("*** testPingExcluded ***");
        Context jndi=null;

        try {
        	jndi = runAs(knownUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }
        
        try {
        	jndi = runAs(userUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }        

        try {
        	jndi = runAs(adminUser);
        	SecurePingClient ejb=(SecurePingClient)jndi.lookup(jndiName);
            logger.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }        
    }      
}
