package ejava.examples.ejbsessionbank.ejbclient;

import ejava.examples.ejbsessionbank.ejb.StatsRemote;
import ejava.examples.ejbsessionbank.ejb.TellerRemote;
import ejava.util.ejb.EJBClient;
import ejava.util.jndi.JNDIUtil;

/**
 * This RMI test uses the legacy jboss-remoting mechanism to communicate 
 * with the EJB. Simply put -- the jboss-remoting mechanism is a generic
 * remote interface that does not directly understand anything it speaks to.
 * Every serialized object is an opaque object that is only understood by 
 * the client that is looking it up. This test assumes there is a 
 * jndi.properties file in the classpath with the following information
 * to speak with the server.<p/>
<pre>
java.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory
java.naming.provider.url=remote://127.0.0.1:4447
java.naming.factory.url.pkgs=
java.naming.security.principal=known
java.naming.security.credentials=password
jboss.naming.client.ejb.context=true
</pre></p>
 * The security properties are needed when the server is configured to 
 * require an authenticated principle to connect. Since we are not dealing
 * with security roles, etc. in this project -- we will assign a single,
 * group login credential in the jndi.properties for all clients to share.
 */
public class TellerRemotingIT extends TellerAccountITBase {
    /*
     * The remote lookup name is a general purpose name that the EJB remote
     * interface is registered under for this type of client to connect to.
     * It is exported using the following name on the server
     * 
     java:jboss/exported/(ear)/(module)/(ejbClass)!(remoteInterface)

	 * but remote clients look it up with just the name part starting after 
	 * exported
	 * 
	 (ear)/(module)/(ejbClass)!(remoteInterface)
     */
    public static final String jndiName = System.getProperty("jndi.name.remoting",
    	EJBClient.getRemoteLookupName("ejbsessionBankEAR", "ejbsessionBankEJB", 
    		"TellerEJB", TellerRemote.class.getName()));
    public static final String statsJNDI = System.getProperty("stats.jndi.remoting",
    	EJBClient.getRemoteLookupName("ejbsessionBankEAR", "ejbsessionBankEJB", 
			"StatsEJB", StatsRemote.class.getName()));
    
    /**
     * Initializes the parent class' teller remote reference using 
     * an InitialContext based on jboss-remoting.
     */
    @Override
    public void setUp() throws Exception {
        super.jndiName = jndiName;
        super.statsJNDI = statsJNDI;
        super.setUp();
    }
}
