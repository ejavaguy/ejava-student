package info.ejava.examples.ejb.basicejb.ejbclient;

import info.ejava.examples.ejb.basic.ejb.GreeterRemote;

/**
 * This RMI test uses the newer JBoss EJBClient mechanism for communicating with
 * the EJB. Simply put -- the JBoss EJBClient technique knows that the
 * server-side object is an EJB and could be stateless or stateful. With that
 * knowledge it provides extra efficiencies in communication and states there
 * are even finer grain controls that could be applied because it has that
 * knowledge. 
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
        super.jndiName = EAR_EJBCLIENT_JNDINAME;
        super.setUp();
    }
}
