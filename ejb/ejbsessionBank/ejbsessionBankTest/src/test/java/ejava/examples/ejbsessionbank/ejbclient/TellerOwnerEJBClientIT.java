package ejava.examples.ejbsessionbank.ejbclient;

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
        super.jndiName = TellerEJBClientIT.jndiName;
        super.statsJNDI = TellerEJBClientIT.statsJNDI;
        super.setUp();
    }
}
