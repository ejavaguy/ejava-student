package info.ejava.examples.secureping;

import java.util.Arrays;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class flushes the JBoss JaasSecurityManager authentication cache. 
 * This needs to be done after changing user login/role information in the 
 * database when not waiting not waiting for a timeout supplied in 
 * deploy/security-service.xml. It does so using the RMIAdapter for the 
 * JMX MBean server.
 */
public class ResetAuthenticationCache {
    private static final Logger logger = LoggerFactory.getLogger(ResetAuthenticationCache.class); 
    private static final String SECURITY_MANAGER_SERVICE = 
        "jboss.security:service=JaasSecurityManager";
    private static final String FLUSH_OPERATION = 
        "flushAuthenticationCache";
    private static final String jndiName = 
        System.getProperty("jmx.invoker","jmx/invoker/RMIAdaptor");
    private static final String domainName = 
        System.getProperty("jmx.domain", "ejavaDomain");

    
    public ResetAuthenticationCache() {}
    
    public void execute() throws Exception {
        //get the JMX Adaptor from the JNDI tree
        Context jndi = new InitialContext(); //rely on a jndi.properties file
        logger.debug("jndi=" + jndi.getEnvironment());
        logger.debug("looking up:" + jndiName);
        Object object = jndi.lookup(jndiName);
        //RMIAdaptor remote = (RMIAdaptor) object;
        MBeanServerConnection remote = (MBeanServerConnection) object;
        
        
        //invoke the bean to flush the authentication cache for the domain
        ObjectName name = new ObjectName(SECURITY_MANAGER_SERVICE);
        Object[] params = { domainName };
        String[] signature = { "java.lang.String" };
        logger.debug(name + "." + FLUSH_OPERATION + "(" + 
                Arrays.toString(params) + ")");
        remote.invoke(name,FLUSH_OPERATION, params, signature);
    }
    
    public static void main(String args[]) {
        try {
            new ResetAuthenticationCache().execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
