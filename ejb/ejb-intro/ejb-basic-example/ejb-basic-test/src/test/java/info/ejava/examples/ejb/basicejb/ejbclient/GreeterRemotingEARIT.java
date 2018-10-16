package info.ejava.examples.ejb.basicejb.ejbclient;

import info.ejava.examples.ejb.basic.ejb.GreeterRemote;

/**
 * This RMI test uses the legacy jboss-remoting mechanism to communicate with
 * the EJB. Simply put -- the jboss-remoting mechanism is a generic remote
 * interface that does not directly understand anything it speaks to. Every
 * serialized object is an opaque object that is only understood by the client
 * that is looking it up. This test assumes there is a jndi.properties file in
 * the classpath with the following information to speak with the server.
 */

public class GreeterRemotingEARIT extends GreeterBase {
    /**
     * The remote lookup name is a general purpose name that the EJB remote
     * interface is registered under for this type of client to connect to. It
     * is exported using the following name on the server
     * 
     * java:jboss/exported/(ear)/(module)/(ejbClass)!(remoteInterface)
     * 
     * but remote clients look it up with just the name part starting after
     * exported
     * 
     * (ear)/(module)/(ejbClass)!(remoteInterface)
     */
    public static final String EAR_REMOTING_JNDINAME = System.getProperty(
            "jndi.name.remoting.ear", "ejb-basic-ear/ejb-basic-ejb/GreeterEJB!"
                    + GreeterRemote.class.getName());

    /**
     * Initializes the parent class' to use JBoss Remoting and access EAR
     * deployment.
     */
    @Override
    public void setUp() throws Exception {
        super.jndiName = EAR_REMOTING_JNDINAME;
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        // don't call close on JNDI context
    }
}
