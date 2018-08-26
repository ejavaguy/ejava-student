package info.ejava.examples.secureping.ejbclient;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbAnnotation;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.yasson.internal.model.JsonbAnnotated;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ejava.examples.secureping.client.AcceptFilter;
import info.ejava.examples.secureping.client.BasicAuthnFilter;
import info.ejava.examples.secureping.client.LoggingFilter;
import info.ejava.examples.secureping.client.SecurePingJaxRsClient;
import info.ejava.examples.secureping.dto.PingResult;

/**
 * This class demonstrates accessing the application server and EJB using
 * a JAX-RS as different authenticated users. Since we are switching users
 * and using filters to do so -- we will build a Map of clients. Each client
 * will be configured for the properties of our tests as well as the login-specific
 * details for a particular client.
 */
public class SecurePingJaxRsIT extends SecurePingTestBase {
    private static final Logger logger = LoggerFactory.getLogger(SecurePingJaxRsIT.class);
    String baseUrlString = System.getProperty("url.base.secureping",
            "https://localhost:8443/securePingApi/api/");
    String mediaType = System.getProperty("type.media.secureping", "application/json");
    private URI baseUrl;
    private Object[] mediaTypes;
    private Map<String, SecurePingJaxRsClient> pingClients = new HashMap<>();
    
    @Before
    public void setUpJaxRs() throws URISyntaxException {
        baseUrl = new URI(baseUrlString);
        //some replies will come back as plain text
        mediaTypes = new Object[] { mediaType, MediaType.TEXT_PLAIN};
    }
    
    /*
     * This method will either build a new JAX-RS Client or return an existing
     * one that has been configured for user with the provided credentials
     * and other settings of the test.
     */
    private SecurePingJaxRsClient runAs(String[] login) {
        //initialize the credentials on a per-credential basis
        String key = login==null ? "anonymous" : login[0] + login[1].hashCode();
        SecurePingJaxRsClient pingClient = pingClients.get(key); 
        if (pingClient==null) {
            Client jaxRsClient = ClientBuilder.newClient();
            if (login!=null) {
                jaxRsClient.register(new BasicAuthnFilter(login[0], login[1]));
            }
            if (logger.isDebugEnabled()) {
                jaxRsClient.register(new LoggingFilter(logger));
            }
            jaxRsClient.register(new AcceptFilter(mediaTypes));
            pingClient = new SecurePingJaxRsClient(jaxRsClient, baseUrl);
            pingClients.put(key, pingClient);
        }
        return pingClient;
    }
    
    //a dummy class to be able to express JSON-B in marshaling an un-annotated DTO class
    @JsonbAnnotation private class UseJsonb {};
    
    private <T> T getEntity(Response response, Class<T> type) throws Exception {
        if (Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return response.readEntity(type, UseJsonb.class.getAnnotations());
        } else {
            throw new Exception(String.format("error response[%s]: %s",
                    response.getStatusInfo(),
                    response.readEntity(String.class))
                    );
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

        SecurePingJaxRsClient securePing = runAs(null);
        try {
            String name = securePing.whoAmI().readEntity(String.class);
            logger.debug("anonymous user whoAmI={}", name);
        } catch (Exception ex) {
            logger.debug("expected exception:{}", ex.getMessage());
        }
        
        securePing = runAs(knownLogin);
        assertEquals("unexpected user", knownUser, securePing.whoAmI().readEntity(String.class));
        
        securePing = runAs(userLogin);
        assertEquals("unexpected user", userUser, securePing.whoAmI().readEntity(String.class));
        
        securePing = runAs(adminLogin);
        assertEquals("unexpected user", adminUser, securePing.whoAmI().readEntity(String.class));
    }

    /**
     * This test verifies the caller is in the expected roles. Although the client
     * participates in authentication, all role mapping is on the server-side.
     * @throws Exception
     */
    @Test
    public void testIsCallerInRole() throws Exception {
        logger.info("*** testIsCallerInRole ***");

        SecurePingJaxRsClient securePing = runAs(knownLogin);
        assertFalse("known in admin role", getEntity(securePing.isCallerInRole("admin"), Boolean.class));
        assertFalse("known in user role",  getEntity(securePing.isCallerInRole("user"), Boolean.class));
        assertFalse("known in internalRole role", getEntity(securePing.isCallerInRole("internalRole"), Boolean.class));
                
        securePing = runAs(userLogin);
        assertFalse("user in admin role",   getEntity(securePing.isCallerInRole("admin"), Boolean.class));
        assertTrue("user not in user role", getEntity(securePing.isCallerInRole("user"), Boolean.class));
        assertFalse("user in internalRole role", getEntity(securePing.isCallerInRole("internalRole"), Boolean.class));

        securePing = runAs(adminLogin);
        assertTrue("admin not in admin role", getEntity(securePing.isCallerInRole("admin"), Boolean.class));
        assertTrue("admin not in user role",  getEntity(securePing.isCallerInRole("user"), Boolean.class));
        assertTrue("admin not in internalRole role", getEntity(securePing.isCallerInRole("internalRole"), Boolean.class));
    }

    /**
     * This test verifies the different users can call a method annotated with @PermitAll
     * @throws Exception
     */
    @Test
    public void testPingAll() throws Exception {
        logger.info("*** testPingAll ***");
        try {
            SecurePingJaxRsClient securePing = runAs(null);
            PingResult result = getEntity(securePing.pingAll(null), PingResult.class);
            logger.info("{}", result);
            assertEquals("unexpected result for known",
                    "called pingAll, principal=anonymous, isUser=false, isAdmin=false, isInternalRole=false",
                    result.getServiceResult());
            assertFalse("is user", result.getIsUser());
            assertFalse("is admin", result.getIsAdmin());
        } 
        catch (Exception ex) {
            logger.info("expected error annonymous calling pingAll:" + ex);
            logger.info("error calling pingAll for anonymous", ex);
            fail("error calling pingAll for anonymous:" +ex);
        }

        try {
            SecurePingJaxRsClient securePing = runAs(knownLogin);
            PingResult result = getEntity(securePing.pingAll(null), PingResult.class);
            logger.info("{}", result);
            assertEquals("unexpected result for known",
            		"called pingAll, principal=known, isUser=false, isAdmin=false, isInternalRole=false",
            		result.getServiceResult());
            assertFalse("is user", result.getIsUser());
            assertFalse("is admin", result.getIsAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAll for known", ex);
            fail("error calling pingAll for known:" +ex);
        }

        try {
            SecurePingJaxRsClient securePing = runAs(userLogin);
            PingResult result = getEntity(securePing.pingAll(null), PingResult.class);
            logger.info("{}", result);
            assertEquals("unexpected result for known",
                String.format("called pingAll, principal=%s, isUser=true, isAdmin=false, isInternalRole=false", userUser),
                result.getServiceResult());
            assertTrue("not user", result.getIsUser());
            assertFalse("is admin", result.getIsAdmin());
        }
        catch (Exception ex) {
            logger.info("error calling pingAll for user", ex);
            fail("error calling pingAll for user:" +ex);
        }        

        try {
            SecurePingJaxRsClient securePing = runAs(adminLogin);
            PingResult result = getEntity(securePing.pingAll(null), PingResult.class);
            logger.info("{}", result);
            assertEquals("unexpected result for known",
                String.format("called pingAll, principal=%s, isUser=true, isAdmin=true, isInternalRole=true", adminUser),
                result.getServiceResult());
            assertTrue("not user", result.getIsUser());
            assertTrue("not admin", result.getIsAdmin());
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
    @Test
    public void testPingUser() throws Exception {
        logger.info("*** testPingUser ***");
        try {
            SecurePingJaxRsClient securePing = runAs(null);
            PingResult result = getEntity(securePing.pingUser(null), PingResult.class);
            logger.info("{}", result);
            
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
     */

    /**
     * This test verifies the ability to control access to methods restricted
     * to the admin role
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
     */

    /**
     * This method verifies the ability to restrict all from accessing
     * a method annotated as excluded.
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
     */
}
