package ejava.examples.ejbsessionbank.ejbclient;

import ejava.util.jndi.JNDIUtil;

/**
 * This class sets up the parent class for testing with a remote interface
 * obtained through the jboss-ejb-client mechanism.
 */
public class TellerOwnerEJBClientIT extends TellerOwnerITBase {
    /**
     * Initializes the parent class' teller remote reference using 
     * an InitialContext based on jboss-remoting.
     */
    @Override
    public void setUp() throws Exception {
        super.jndiProperties = JNDIUtil.getJNDIProperties("jboss.ejbclient.");
        super.jndiName = TellerEJBClientIT.jndiName;
        super.statsJNDI = TellerEJBClientIT.statsJNDI;
        super.setUp();
    }
}
