package info.ejava.examples.secureping.ejbclient;


import static org.junit.Assert.*;
import info.ejava.examples.secureping.ejb.SecurePingRemote;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

//import org.jboss.ejb.client.ContextSelector;
//import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
//import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
//import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.util.jndi.JNDIUtil;

/**
 * This class demonstrates accessing the application server and EJB using
 * a EJBClient as different authenticated users. Since we are switching users
 * and EJBClient ties the identity to the connection -- we have to make use of 
 * a proprietary technique to change users.
</pre>
 */
public class SecurePingEJBClientIT extends SecurePingTestBase {
    private static final Logger logger = LoggerFactory.getLogger(SecurePingEJBClientIT.class);
    String jndiName = System.getProperty("jndi.name.secureping.ejbclient",
            "ejb:securePingEAR/securePingEJB/SecurePingEJB!"+SecurePingRemote.class.getName());    
    private SecurePingRemote securePing;
    
    /*
     * This method will change the callback handler to the specified instance
     * and force EJBClient to reload the connection. This is required so that
     * the CallbackHandler is consulted for the new identity.
     * @See https://developer.jboss.org/message/730760
     */
    private void runAs(String[] login) throws NamingException, IOException {
        if (!Arrays.equals(login, currentLogin) || securePing==null) {
            Properties props = new Properties();
            if (login!=null) {
                props.put(Context.SECURITY_PRINCIPAL, login[0]);
                props.put(Context.SECURITY_CREDENTIALS, login[1]);
            }
            InitialContext jndi = null;
            try {
                jndi = new InitialContext(props);
                logger.debug("looking up jndi.name={} as {}", jndiName, login==null?"anonymous" : login[0]);
                securePing = (SecurePingRemote)jndi.lookup(jndiName);
                logger.debug("found={}", securePing);
                logger.debug("login={}, whoAmI={}", login==null ? null : Arrays.toString(login), securePing.whoAmI());
                currentLogin = login;
            } finally {
                if (jndi!=null) { jndi.close(); }
            }
        }
    }
    
    /**
     * This test verifies the ability to login as a specific user and have the 
     * server-side identify the client as the proper user at runtime. 
     * @throws Exception
     */
    @Test 
    public void testLogin() throws Exception {
        logger.info("*** testLogin ***");

        runAs(null);
        try {
            String name = securePing.whoAmI();
            logger.debug("anonymous user whoAmI=", name);
        } catch (Exception ex) {
            logger.debug("expected exception:{}", ex.getMessage());
        }
        
        runAs(knownLogin);
        assertEquals("unexpected user", knownUser, securePing.whoAmI());
        
        runAs(userLogin);
        assertEquals("unexpected user", userUser, securePing.whoAmI());
        
        runAs(adminLogin);
        assertEquals("unexpected user", adminUser, securePing.whoAmI());
    }

    /**
     * This test verifies the caller is in the expected roles. Although the client
     * participates in authentication, all role mapping is on the server-side.
     * @throws Exception
     */
    @Test
    public void testIsCallerInRole() throws Exception {
        logger.info("*** testIsCallerInRole ***");

        runAs(knownLogin);
        assertFalse("known in admin role", securePing.isCallerInRole("admin"));
        assertFalse("known in user role",  securePing.isCallerInRole("user"));
        assertFalse("known in internalRole role", securePing.isCallerInRole("internalRole"));
                
        runAs(userLogin);
        assertFalse("user in admin role",   securePing.isCallerInRole("admin"));
        assertTrue("user not in user role", securePing.isCallerInRole("user"));
        assertFalse("user in internalRole role", securePing.isCallerInRole("internalRole"));

        runAs(adminLogin);
        assertTrue("admin not in admin role", securePing.isCallerInRole("admin"));
        assertTrue("admin not in user role",  securePing.isCallerInRole("user"));
        assertTrue("admin not in internalRole role", securePing.isCallerInRole("internalRole"));
    }

    /**
     * This test verifies the different users can call a method annotated with @PermitAll
     * @throws Exception
     */
    @Test
    public void testPingAll() throws Exception {
        logger.info("*** testPingAll ***");
        try {
        	    runAs(null);
            logger.info(securePing.pingAll());
            //fail("anonymous user not detected"); 
        } 
        catch (Exception ex) {
            logger.info("expected error annonymous calling pingAll:" + ex);
        }

        try {
            runAs(knownLogin);
            String result = securePing.pingAll();
            logger.info(result);
            assertEquals("unexpected result for known",
        		"called pingAll, principal=known, isUser=false, isAdmin=false, isInternalRole=false",
        		result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll for known", ex);
            fail("error calling pingAll for known:" +ex);
        }
        
        try {
            runAs(userLogin);
            String result = securePing.pingAll();
            logger.info(result);
            assertEquals("unexpected result for admin",
        		String.format("called pingAll, principal=%s, isUser=true, isAdmin=false, isInternalRole=false", userUser),
        		result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll for user", ex);
            fail("error calling pingAll for user:" +ex);
        }        

        try {
        	runAs(adminLogin);
            String result=securePing.pingAll();
            logger.info(result);
            assertEquals("unexpected result for admin",
        		String.format("called pingAll, principal=%s, isUser=true, isAdmin=true, isInternalRole=true", adminUser),
        		result);
        }
        catch (Exception ex) {
            logger.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        
    }

    /**
     * This test verifies the ability to control access to methods restricted 
     * to the user role.
     * @throws Exception
     */
    @Test
    public void testPingUser() throws Exception {
        logger.info("*** testPingUser ***");
        try {
            logger.info(securePing.pingUser());
            fail("didn't detect anonymous user");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }

        try {
            runAs(knownLogin);
            logger.info(securePing.pingUser());
            fail("didn't detect known, but non-user");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }
        
        try {
            runAs(userLogin);
            logger.info(securePing.pingUser());
        }
        catch (Exception ex) {
            logger.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        

        try {
            runAs(adminLogin);
            logger.info(securePing.pingUser());
        }
        catch (Exception ex) {
            logger.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        
    }

    /**
     * This test verifies the ability to control access to methods restricted
     * to the admin role
     */
    @Test
    public void testPingAdmin() throws Exception {
        logger.info("*** testPingAdmin ***");
        try {
            logger.info(securePing.pingAdmin());
            fail("didn't detect anonymous user");
        }
        catch (Exception ex) {
            logger.info("expected exception thrown:" + ex);
        }

        try {
            runAs(knownLogin);
            logger.info(securePing.pingAdmin());
            fail("didn't detect known, but non-admin user");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }
        
        try {
            runAs(userLogin);
            logger.info(securePing.pingAdmin());
            fail("didn't detect non-admin user");
        }
        catch (EJBAccessException ex) {
            logger.info("expected exception thrown:" + ex);
        }        

        try {
            runAs(adminLogin);
            logger.info(securePing.pingAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        
    }

    /**
     * This method verifies the ability to restrict all from accessing
     * a method annotated as excluded.
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
