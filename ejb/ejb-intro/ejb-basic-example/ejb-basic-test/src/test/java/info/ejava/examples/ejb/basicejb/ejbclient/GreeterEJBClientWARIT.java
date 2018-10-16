package info.ejava.examples.ejb.basicejb.ejbclient;

import info.ejava.examples.ejb.basic.ejb.GreeterRemote;

/**
 * This class sets up the base IT test to use EJB Client and access the
 * WAR-deployed EJB.
 */
public class GreeterEJBClientWARIT extends GreeterBase {
    public static final String WAR_EJBCLIENT_JNDINAME = System.getProperty(
            "jndi.name.ejbclient.war", "ejb:/ejb-basic-war/GreeterEJB!"
                    + GreeterRemote.class.getName());

    @Override
    public void setUp() throws Exception {
        super.jndiName = WAR_EJBCLIENT_JNDINAME;
        super.setUp();
    }
}
