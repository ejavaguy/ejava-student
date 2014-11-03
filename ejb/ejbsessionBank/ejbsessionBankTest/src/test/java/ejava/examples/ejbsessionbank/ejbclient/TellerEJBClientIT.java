package ejava.examples.ejbsessionbank.ejbclient;

import ejava.examples.ejbsessionbank.ejb.StatsRemote;
import ejava.examples.ejbsessionbank.ejb.TellerRemote;
import ejava.util.ejb.EJBClient;
import ejava.util.jndi.JNDIUtil;

/**
 * This RMI test uses the newer JBoss EJBClient mechanism for communicating with
 * the EJB. Simply put -- the JBoss EJBClient technique knows that the 
 * server-side object is an EJB and could be stateless or stateful. With that
 * knowledge it provides extra efficiencies in communication and states there
 * are even finer grain controls that could be applied because it has that 
 * knowledge. This test assumes there is a jndi.properties and jboss-
 * jndi.properties file in the classpath with the following information.<p/>
<pre>
jboss.ejbclient.java.naming.factory.initial=
jboss.ejbclient.java.naming.provider.url=
jboss.ejbclient.java.naming.factory.url.pkgs=org.jboss.ejb.client.naming
</pre></p>
 * There is also expected to be a jboss-ejb-client.properties file<p/>
<pre>
remote.connections=default
remote.connection.default.host=127.0.0.1
remote.connection.default.port=4447
remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED=false
</pre><p/>
 * The security properties are not necessary for this technique since it 
 * is aware we are communicating with an EJB and the EJB is not configured
 * with a security-domain. 
 */
public class TellerEJBClientIT extends TellerAccountITBase {
    /*
     * The remote lookup name is specific to JBoss EJBs. The name in the
     * server is of the following form:<p/>
<pre> 
java:jboss/exported/(ear)/(module)/(ejbClass)!(remoteInterface)	 
</pre>
	 * 
	 * but remote clients look it up with just the name part starting after 
	 * exported<p/>
<pre> 
ejb:(ear)/(module)/(distinctName)/(ejbClass)!(remoteInterface)
ejb:(ear)/(module)/(distinctName)/(ejbClass)!(remoteInterface)?stateful
</pre>
     */
    public static final String jndiName = System.getProperty("jndi.name.ejbclient",
    	EJBClient.getEJBClientLookupName("ejbsessionBankEAR", "ejbsessionBankEJB", 
			"","TellerEJB", TellerRemote.class.getName(), false));
    public static final String statsJNDI = System.getProperty("stats.jndi.ejbclient",
    	EJBClient.getEJBClientLookupName("ejbsessionBankEAR", "ejbsessionBankEJB", 
			"","StatsEJB", StatsRemote.class.getName(), false));
    
    /**
     * Initializes the parent class' teller remote reference using 
     * an InitialContext configured for jboss-ejb-client.
     */
    @Override
    public void setUp() throws Exception {
        super.jndiProperties = JNDIUtil.getJNDIProperties("jboss.ejbclient.");
        super.jndiName = TellerEJBClientIT.jndiName;
        super.statsJNDI = TellerEJBClientIT.statsJNDI;
        super.setUp();
    }
}
