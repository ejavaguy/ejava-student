package info.ejava.examples.ejb.basicejb.ejbclient;

import info.ejava.examples.ejb.basic.ejb.GreeterRemote;

/**
 * This class sets up the base IT test to use JBoss Remoting and access the
 * WAR-based EJB deployment.
 */
public class GreeterRemotingWARIT extends GreeterBase {
    public static final String WAR_REMOTING_JNDINAME = System.getProperty(
            "jndi.name.remoting.war", "ejb-basic-war/GreeterEJB!"
                    + GreeterRemote.class.getName());

    @Override
    public void setUp() throws Exception {
        super.jndiName = WAR_REMOTING_JNDINAME;
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        // don't call close on JNDI context
    }
}
