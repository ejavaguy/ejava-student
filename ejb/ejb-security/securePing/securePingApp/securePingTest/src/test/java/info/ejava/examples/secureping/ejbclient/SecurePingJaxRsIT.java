package info.ejava.examples.secureping.ejbclient;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbAnnotation;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
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
    
    //@BeforeClass
    public static void check() {
        String trustStore = System.getProperty("javax.net.ssl.trustStore");
        logger.info("trustStore={}", trustStore);
        Assume.assumeTrue("no trustStore specified, we cannot run this testcase", trustStore!=null);
        File f = new File(trustStore);
        assertTrue("trustStore path does not exist: " + trustStore, f.exists());
        assertTrue("trustStore path is not readable: " + trustStore, f.canRead());
    }
    
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
    
    private <T> T getEntity(Response response, Class<T> type) {
        if (Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return response.readEntity(type, UseJsonb.class.getAnnotations());
        } else {
            throw new IllegalStateException(String.format("error response[%s]: %s",
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
    public void testLogin() {
        logger.info("*** testLogin ***");

        SecurePingJaxRsClient securePing = runAs(null);
        String name = securePing.whoAmI().readEntity(String.class);
        logger.debug("anonymous user whoAmI={}", name);
        assertEquals("unexpected user", "anonymous", name);
        
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
    public void testIsCallerInRole() {
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
     * In this test, we will be calling a series of methods against endpoints in the JaxRS facade
     * that are unprotected. They will allow non-HTTPS connections and require no authentication to 
     * invoke. However, they will be invoking a protected EJB in the back-end -- so the call will not
     * go any further than the JaxRS facade if the caller is not authorized for the EJB action.
     */
    @Test
    public void testUnsecuredJaxRs() {
        logger.info("*** testUnsecuredJaxRs() ***");

        for (String[] login: new String[][] {null, knownLogin, userLogin, adminLogin}) {
            String name = login==null ? "anonymous" : login[0];
            logger.info("processing {}", name);
            SecurePingJaxRsClient securePing = runAs(login);
            PingResult result = getEntity(securePing.pingAll("unsecured"), PingResult.class); //<== UNSECURED JAX-RS endpoint
            logger.info("{}", result);
            boolean isUser = login==null ? false : Arrays.asList("admin1", "user1").contains(login[0]);
            boolean isAdmin = login==null ? false : Arrays.asList("admin1").contains(login[0]);
            boolean isInternalRole = login==null ? false : Arrays.asList("admin1").contains(login[0]);
            String expectedStr = String.format("called pingAll, principal=%s, isUser=%s, isAdmin=%s, isInternalRole=%s", 
                    name, isUser, isAdmin, isInternalRole);
            assertEquals("unexpected result for " + name, expectedStr,result.getServiceResult());
            assertEquals(name + " is user", isUser, result.getIsUser());
            assertEquals(name + " is admin", isAdmin, result.getIsAdmin());
        }
    }

    /**
     * This test will attempt to invoke the same EJB methods as the testUnsecuredJaxRs test, except 
     * this time we will invoke each through a secured endpoint in the JaxRS facade. This endpoint 
     * requires HTTPS and an identity that is in either the admin or user role.
     */
    @Test
    public void testSecuredJaxRs() {
        logger.info("*** testSecuredJaxRs() ***");

        for (String[] login: new String[][] {null, knownLogin, userLogin, adminLogin}) {
            String name = login==null ? "anonymous" : login[0];
            logger.info("processing {}", name);
            SecurePingJaxRsClient securePing = runAs(login);
            Response response = securePing.pingAll("secured");  //<== SECURED JAX-RS endpoint
            if (login!=null && Arrays.asList("admin1", "user1").contains(login[0])) {
                /*
                 * These users are authorized by the JaxRS web facade and can make correct calls to back-end EJB
                 */
                PingResult result = getEntity(response, PingResult.class);
                logger.info("{}", result);
                boolean isUser = login==null ? false : Arrays.asList("admin1", "user1").contains(login[0]);
                boolean isAdmin = login==null ? false : Arrays.asList("admin1").contains(login[0]);
                boolean isInternalRole = login==null ? false : Arrays.asList("admin1").contains(login[0]);
                String expectedStr = String.format("called pingAll, principal=%s, isUser=%s, isAdmin=%s, isInternalRole=%s", 
                        name, isUser, isAdmin, isInternalRole);
                assertEquals("unexpected result for " + name, expectedStr,result.getServiceResult());
                assertEquals(name + " is user", isUser, result.getIsUser());
                assertEquals(name + " is admin", isAdmin, result.getIsAdmin());
            } else if (Response.Status.Family.CLIENT_ERROR.equals(response.getStatusInfo().getFamily())) {
                /*
                 * These users are not authorized by the JaxRS web facade and will get an error payload from container
                 */
                String payload = response.readEntity(String.class);
                logger.debug("received expected error {} {} for {},\n{}", 
                        response.getStatus(), response.getStatusInfo(), name, payload);
                if (login==null) {
                    //unknown == UNAUTHORIZED
                    assertEquals(String.format("unexpected error response for %s", name), 
                            Response.Status.UNAUTHORIZED, response.getStatusInfo());
                    assertTrue("response may not have come from container", payload.contains("<body>Unauthorized</body>"));
                } else {
                    //known but no access == FORBIDDEN
                    assertEquals(String.format("unexpected error response for %s", name), 
                            Response.Status.FORBIDDEN, response.getStatusInfo());
                    assertTrue("response may not have come from container", payload.contains("<body>Forbidden</body>"));
                }
            } else {
                /*
                 * We received a non CLIENT_ERROR - fail the test
                 */
                String payload = response.readEntity(String.class);
                logger.debug("received unexpected status {} {} for {},\n{}", 
                        response.getStatus(), response.getStatusInfo(), name, payload);
                fail(String.format("received unexpected status %s for %s", name, response.getStatus()));
            }
        }
    }
    
    /**
     * This test will verify that the EJB tier will be invoked with the caller's identity
     * and will be able to make security decisions based on the user's role. The is the same 
     * sort of test as testUnsecuredJaxRs except now we are calling a few methods that are 
     * not allowed at the EJB layer. 
     */
    @Test
    public void testUnsecuredJaxRsCallingConstrainedMethod() {
        logger.info("*** testUnsecuredJaxRsCallingConstrainedMethod() ***");
        
        for (String[] login: new String[][] {null, knownLogin, userLogin, adminLogin}) {
            String name = login==null ? "anonymous" : login[0];
            logger.info("processing {}", name);
            SecurePingJaxRsClient securePing = runAs(login);
            Response response = securePing.pingAdmin("unsecured");  //<== UNSECURED JAX-RS endpoint
                //all results are handled internal to JaxRS facade and everything gets a response payload
            PingResult result = response.readEntity(PingResult.class, UseJsonb.class.getAnnotations());
            logger.info("{}", result);
            boolean isUser = login==null ? false : Arrays.asList("admin1", "user1").contains(login[0]);
            boolean isAdmin = login==null ? false : Arrays.asList("admin1").contains(login[0]);
            boolean isInternalRole = login==null ? false : Arrays.asList("admin1").contains(login[0]);

            if (login!=null && Arrays.asList("admin1").contains(login[0])) {
                /*
                 * These users are authorized by the JaxRS web facade and can make correct calls to back-end EJB
                 */
                String expectedStr = String.format("called pingAdmin, principal=%s, isUser=%s, isAdmin=%s, isInternalRole=%s", 
                        name, isUser, isAdmin, isInternalRole);
                assertEquals("unexpected result for " + name, expectedStr,result.getServiceResult());
                assertEquals(name + " is user", isUser, result.getIsUser());
                assertEquals(name + " is admin", isAdmin, result.getIsAdmin());
            } else if (Response.Status.FORBIDDEN.equals(response.getStatusInfo())) {
                /*
                 * These users were allowed into the JaxRS web facade, but failed to contact the EJB
                 */
                assertTrue("response may not have come from EJB", result.getServiceResult().contains("EJBAccessException"));
            } else {
                fail(String.format("received unexpected status %s for %s", name, response.getStatus()));
            }
        }
    }
    
    /**
     * This test is similar the the previous except that the JaxRS web facade will restrict callers of it
     * to allowed roles at the container level.
     */
    @Test
    public void testSecuredJaxRsCallingConstrainedMethod() {
        logger.info("*** testSecuredJaxRsCallingConstrainedMethod() ***");
        
        for (String[] login: new String[][] {null, knownLogin, userLogin, adminLogin}) {
            String name = login==null ? "anonymous" : login[0];
            logger.info("processing {}", name);
            SecurePingJaxRsClient securePing = runAs(login);
            Response response = securePing.pingAdmin("secured");  //<== SECURED JAX-RS endpoint

            if (login!=null && Arrays.asList("admin1").contains(login[0])) {
                /*
                 * These users are authorized by the JaxRS web facade and can make correct calls to back-end EJB
                 */
                PingResult result = getEntity(response, PingResult.class);
                logger.info("{}", result);
                boolean isUser = login==null ? false : Arrays.asList("admin1", "user1").contains(login[0]);
                boolean isAdmin = login==null ? false : Arrays.asList("admin1").contains(login[0]);
                boolean isInternalRole = login==null ? false : Arrays.asList("admin1").contains(login[0]);
                String expectedStr = String.format("called pingAdmin, principal=%s, isUser=%s, isAdmin=%s, isInternalRole=%s", 
                        name, isUser, isAdmin, isInternalRole);
                assertEquals("unexpected result for " + name, expectedStr,result.getServiceResult());
                assertEquals(name + " is user", isUser, result.getIsUser());
                assertEquals(name + " is admin", isAdmin, result.getIsAdmin());
            } else if (Response.Status.Family.CLIENT_ERROR.equals(response.getStatusInfo().getFamily())) {
                /*
                 * These users are not authorized by the JaxRS web facade and will get an error payload from container
                 */
                String payload = response.readEntity(String.class);
                logger.debug("received expected error {} {} for {},\n{}", 
                        response.getStatus(), response.getStatusInfo(), name, payload);
                if (login==null) {
                    //unknown == UNAUTHORIZED
                    assertEquals(String.format("unexpected error response for %s", name), 
                            Response.Status.UNAUTHORIZED, response.getStatusInfo());
                    assertTrue("response may not have come from container", payload.contains("<body>Unauthorized</body>"));
                } else {
                    //known but no access == FORBIDDEN
                    assertEquals(String.format("unexpected error response for %s", name), 
                            Response.Status.FORBIDDEN, response.getStatusInfo());
                    assertTrue("response may not have come from container", payload.contains("<body>Forbidden</body>"));
                }
            } else {
                String payload = response.readEntity(String.class);
                logger.debug("received unexpected status {} {} for {},\n{}", 
                        response.getStatus(), response.getStatusInfo(), name, payload);
                fail(String.format("received unexpected status %s for %s", name, response.getStatus()));
            }
        }
    }
}
