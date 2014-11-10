package info.ejava.examples.securepingclient.ejbclient;


import static org.junit.Assert.*;
import info.ejava.examples.secureping.ejb.SecurePingClient;
import info.ejava.examples.secureping.ejb.SecurePingClientRemote;
import info.ejava.examples.secureping.ejb.SecurePingRemote;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.CallbackHandler;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs a check of the SecurePingClientEJB using RMI Calls.
 * The SecurePingClientEJB lets anyone in and then performs the same
 * action on the SecurePingEJB using a run-as admin1 identity and admin role.
 */
public class SecurePingClientEJBClientIT extends SecurePingClientTestBase {
    private static final Logger logger = LoggerFactory.getLogger(SecurePingClientEJBClientIT.class);
    private static final String jndiName = System.getProperty("jndi.name.securepingclient.ejbclient",
        "ejb:securePingClientEAR/securePingClientEJB/SecurePingClientEJB!"+SecurePingClientRemote.class.getName());
    
    //reference to remote interface for SecurePingEJB
    SecurePingClient securePing;
    
    //callback handlers
    CallbackHandler knownLogin;
    CallbackHandler userLogin;
    CallbackHandler adminLogin;
    
    @Before
    public void setUp() throws Exception {
        //create different types of logins
        knownLogin = new BasicCallbackHandler(knownUser, knownPassword);
        userLogin = new BasicCallbackHandler(userUser, userPassword);
        adminLogin = new BasicCallbackHandler(adminUser, adminPassword);
        
        logger.debug("known login=" + knownLogin);
        logger.debug("user login=" + userLogin);
        logger.debug("admin login=" + adminLogin);        
        
        Properties jndiProperties = new Properties();
        jndiProperties.put("jboss.naming.client.ejb.context", false); //override anything we put there for Remoting
        Context jndi = new InitialContext();
        logger.debug("looking up jndi.name=" + jndiName);
        securePing = (SecurePingClientRemote)jndi.lookup(jndiName);
        logger.debug("found=" + securePing);
        jndi.close();
    }
    
    @After
    public void tearDown() throws Exception{
        runAs(null);
    }
    
    /*
     * This method will change the callback handler to the specified instance
     * and force EJBClient to reload the connection. This is required so that
     * the CallbackHandler is consulted for the new identity.
     * @See https://developer.jboss.org/message/730760
     */
    private void runAs(CallbackHandler login) throws NamingException, IOException {
        if (BasicCallbackHandler.getLogin()!=login) {
            InputStream is = getClass().getResourceAsStream("/jboss-ejb-client.properties");
            assertNotNull("unable to locate jboss-ejb-client.properties", is);
            try {
                Properties props = new Properties();
                props.load(is);
                StringWriter sw = new StringWriter();
                props.store(sw,null);
                //logger.debug("props=" + sw);
                BasicCallbackHandler.setLogin(login);
                EJBClientConfiguration cfg = new PropertiesBasedEJBClientConfiguration(props);
                ContextSelector<EJBClientContext> contextSelector = new ConfigBasedEJBClientContextSelector(cfg);
                EJBClientContext.setSelector(contextSelector);
            } finally {
                is.close();
            }
        }
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
        
        try {
            runAs(null);
            assertFalse("anonomous in admin role", securePing.isCallerInRole("admin"));
            assertFalse("anonomous in user role", securePing.isCallerInRole("user"));
            assertFalse("anonomous in internalRole role", securePing.isCallerInRole("internalRole"));
        } catch (Exception ex) {
            logger.info("error calling unauthenticated isCallerInRole:" +ex);
        }
        
        runAs(knownLogin);
        assertFalse("known in admin role", securePing.isCallerInRole("admin"));
        assertFalse("known in user role", securePing.isCallerInRole("user"));
        assertFalse("known in internalRole role", securePing.isCallerInRole("internalRole"));
        
        runAs(userLogin);
        assertFalse("user in admin role", securePing.isCallerInRole("admin"));
        assertTrue("user not in user role", securePing.isCallerInRole("user"));
        assertFalse("user in internalRole role", securePing.isCallerInRole("internalRole"));
        
        runAs(adminLogin);
        assertTrue("admin not in admin role", securePing.isCallerInRole("admin"));
        assertTrue("admin not in user role", securePing.isCallerInRole("user"));
        //securePingClientEJB does not have this role mapped to admin
        assertFalse("admin not in internalRole role", securePing.isCallerInRole("internalRole"));
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
        try {
            logger.info(securePing.pingAll());
        }
        catch (Exception ex) {
            logger.info("error calling pingAll:" + ex, ex);
            //failing on windows??? fail("error calling pingAll:" +ex);
        }

        try {
            runAs(knownLogin);
            String result=securePing.pingAll();
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
            runAs(userLogin);
            String result=securePing.pingAll();
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
            runAs(adminLogin);
            String result=securePing.pingAll();
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
        try {
            logger.info(securePing.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
        }

        try {
            runAs(adminLogin);
            logger.info(securePing.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        
        try {
            runAs(knownLogin);
            logger.info(securePing.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        
        try {
            runAs(userLogin);
            logger.info(securePing.pingAdmin());
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
        try {
            logger.info(securePing.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }

        try {
            runAs(knownLogin);
            logger.info(securePing.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }
        
        try {
            runAs(userLogin);
            logger.info(securePing.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }        

        try {
            runAs(adminLogin);
            logger.info(securePing.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }        
    }      
}
