package ejava.examples.ejbsessionbank.ejbclient;

import ejava.util.jndi.JNDIUtil;

/**
 * This class sets up the parent class for testing with a remote interface
 * obtained through the jboss-remoting mechanism.
 */
public class TellerOwnerRemotingIT extends TellerOwnerITBase {
    /**
     * Initializes the parent class' teller remote reference using 
     * an InitialContext based on jboss-remoting.
     */
    @Override
    public void setUp() throws Exception {
    	super.jndiProperties = JNDIUtil.getJNDIProperties("jboss.remoting.");
    	super.jndiName = TellerRemotingIT.jndiName;
    	super.statsJNDI = TellerRemotingIT.statsJNDI;
        super.setUp();
    }
}
