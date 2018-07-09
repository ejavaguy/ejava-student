package info.ejava.examples.ejb.basicejb.ejbclient;

import info.ejava.examples.ejb.basic.ejb.GreeterRemote;
import ejava.util.jndi.JNDIUtil;

/**
 * This RMI test uses the newer JBoss EJBClient mechanism for communicating with
 * the EJB. Simply put -- the JBoss EJBClient technique knows that the
 * server-side object is an EJB and could be stateless or stateful. With that
 * knowledge it provides extra efficiencies in communication and states there
 * are even finer grain controls that could be applied because it has that
 * knowledge. This test assumes there is a jndi.properties and jboss-
 * jndi.properties file in the classpath with the following information.
 * <p/>
 * 
 * <pre>
jboss.ejbclient.java.naming.factory.initial
jboss.ejbclient.java.naming.provider.url 
jboss.ejbclient.java.naming.factory.url.pkgs = org.jboss.ejb.client.naming
</pre>
 * 
 * </p>
 * There is also expected to be a jboss-ejb-client.properties file
 * <p/>
 * 
 * <pre>
 * remote.connections=default
 * remote.connection.default.host=127.0.0.1
 * remote.connection.default.port=4447
 * remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED=false
 * </pre>
 * <p/>
 * The security properties are not necessary for this technique since it is
 * aware we are communicating with an EJB and the EJB is not configured with a
 * security-domain.
 */
public class GreeterEJBClientEARIT extends GreeterBase {
    /*
     * The remote lookup name is specific to JBoss EJBs. The name in the server
     * is of the following form:<p/> <pre>
     * java:jboss/exported/(ear)/(module)/(ejbClass)!(remoteInterface) </pre>
     * 
     * but remote clients look it up with just the name part starting after
     * exported<p/> <pre>
     * ejb:(ear)/(module)/(distinctName)/(ejbClass)!(remoteInterface)
     * ejb:(ear)/(module)/(distinctName)/(ejbClass)!(remoteInterface)?stateful
     * </pre>
     */
    public static final String EAR_EJBCLIENT_JNDINAME = System.getProperty(
            "jndi.name.ejbclient.ear",
            "ejb:ejb-basic-ear/ejb-basic-ejb/GreeterEJB!"
                    + GreeterRemote.class.getName());

    /**
     * Initializes the parent class to use EJB Client technique and EAR
     * deployment.
     */
    @Override
    public void setUp() throws Exception {
        super.jndiProperties = JNDIUtil.getJNDIProperties("jboss.ejbclient.");
        super.jndiName = EAR_EJBCLIENT_JNDINAME;
        super.setUp();
    }
}
