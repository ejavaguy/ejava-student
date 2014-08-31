package ejava.util.ejb;

/**
 * This class provides utiltity functions for working with an EJB in the JNDI
 * tree. 
 */
public class EJBClient {
    /**
     * This method provides a convenience wrapper around the alternate  
     * getEJBLookupName() helper method. This can be used by EJB clients
     * trying to locate EJBs that are part of a deployment that did not
     * turn off maven version numbers in the deployment. This method assumes
     * the EAR and EJB versions are the same value and will appropriately 
     * append the version to the EAR and EJB names when forming the 
     * jboss-ejb-client JNDI name.
     * @param earName
     * @param ejbModuleName
     * @param distinctName
     * @param ejbClassName
     * @param remoteInterface
     * @param version
     * @return String jndi name that can be used for lookup
     */
	public static String getEJBClientLookupName(
    		String earName,
    		String ejbModuleName,
    		String distinctName,
    		String ejbClassName,
    		String remoteInterface,
    		boolean stateful,
    		String version) {
    	return getEJBClientLookupName(
    			String.format("%s-%s",earName,version), 
    			String.format("%s-%s", ejbModuleName, version), 
    			distinctName, 
    			ejbClassName, 
    			remoteInterface,
    			stateful);
    }
    
	/**
	 * This method returns an remote JNDI name that is usable by the 
	 * jboss-ejb-client org.jboss.ejb.client.naming.ejb.ejbURLContextFactory
	 * context factory. To use this, list "org.jboss.ejb.client.naming"
	 * as one of your java.naming.factory.url.pkgs and be sure to have
	 * org.jboss:jboss-ejb-client listed as a dependency. The physical
	 * name will look like the following in JBoss:<p/>
<pre>
java:jboss/exported/(ear)/(module)/(ejbClass)!(remoteInterface)	 
</pre>
	 * <p/>
	 * JNDI names returned will be in the following form:<p/>
<pre>
ejb:(ear)/(module)/(distinctName)/(ejbClass)!(remoteInterface)
ejb:(ear)/(module)/(distinctName)/(ejbClass)!(remoteInterface)?stateful
</pre><p/>
	 * Where the distinct name is commonly an empty string.<p/>
	 * @param earNameVersion
	 * @param ejbModuleNameVersion
	 * @param distinctName
	 * @param ejbClassName
	 * @param remoteInterface
	 * @return String jndi name that can be used for lookup
	 */
    public static String getEJBClientLookupName(
    		String earNameVersion,
    		String ejbModuleNameVersion,
    		String distinctName,
    		String ejbClassName,
    		String remoteInterface,
    		boolean stateful) {

    	return new StringBuilder("ejb:")
    		.append(earNameVersion).append("/")
    		.append(ejbModuleNameVersion).append("/")
    		.append(distinctName==null?"":distinctName).append("/")
    		.append(ejbClassName).append("!")
    		.append(remoteInterface)
    		.append(stateful?"?stateful" : "")
    		.toString();
    }

    /**
     * This method returns a JNDI name usable with JBoss remote-naming
     * for EJBs deployed within an EAR.<p/>
     * org.jboss.naming.remote.client.InitialContextFactory<p/>
     * The physical JNDI name will be listed as:<p/>
     * <pre>
java:jboss/exported/(ear)/(module)/(ejbClass)!(remoteInterface)
     </pre></p>
     * The name returned will have the following form:<p/>
     * <pre>
(ear)/(module)/(ejbClass)!(remoteInterface)
     * </pre>
     * @param earNameVersion
     * @param ejbModuleNameVersion
     * @param ejbClassName
     * @param remoteInterface
     * @return String jndi name that can be used for lookup
     */
    public static String getRemoteLookupName(
    		String earNameVersion,
    		String ejbModuleNameVersion,
    		String ejbClassName,
    		String remoteInterface) {

    	return new StringBuilder()
    		.append(earNameVersion).append("/")
    		.append(ejbModuleNameVersion).append("/")
    		.append(ejbClassName).append("!")
    		.append(remoteInterface)
    		.toString();
    }
    
    /**
     * This method returns a JNDI name usable with JBoss remote-naming for
     * EJBs deployed within a WAR (i.e., no EAR).<p/>
     * The physical JNDI name will be listed as:<p/>
     * <pre>
java:jboss/exported/(war)/(ejbClass)!(remoteInterface)
     </pre>
     * The name returned will have the following form:<p/>
     * <pre>
(war)/(ejbClass)!(remoteInterface)
     * </pre>
     * @param moduleNameVersion
     * @param ejbClassName
     * @param remoteInterface
     * @return String jndi name that can be used for lookup
     */
    public static String getRemoteLookupName(
    		String moduleNameVersion,
    		String ejbClassName,
    		String remoteInterface) {

    	return new StringBuilder()
    		.append(moduleNameVersion).append("/")
    		.append(ejbClassName).append("!")
    		.append(remoteInterface)
    		.toString();
    }
}
