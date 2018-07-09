package ejava.examples.ejbsessionbank.ejbclient;

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
    	super.jndiName = TellerRemotingIT.jndiName;
    	super.statsJNDI = TellerRemotingIT.statsJNDI;
        super.setUp();
    }
}
