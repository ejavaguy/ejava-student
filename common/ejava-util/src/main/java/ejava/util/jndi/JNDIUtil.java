package ejava.util.jndi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class helps work with the JNDI tree.
 */
public class JNDIUtil {
	private static final Logger logger = LoggerFactory.getLogger(JNDIUtil.class);
	public static final String PROPERTY_FILE="ejava-jndi.properties";
	private static final String[] PATHS = new String[]{
		"/" + PROPERTY_FILE, PROPERTY_FILE
	};
	
	public static InitialContext getInitialContext(String propertiesPath) throws IOException, NamingException {
        InputStream is = null;
        InitialContext jndi = null;
        try {
            //manually load the JNDI properties to make sure we don't get a Jetty JNDI tree in dev
            if ((is=Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesPath))==null) {
                logger.warn("no {} found, check classpath", propertiesPath);
            } else {
                Properties jndiProperties = new Properties();
                jndiProperties.load(is);
                logger.info("jndiProperties={}", jndiProperties);
    
                jndi = new InitialContext(jndiProperties);
            }
          } finally {
            if (is!=null) {
                try { is.close(); } catch(Exception ex) {}
            }
          }
        return jndi;
	}
	
	/**
	 * This method will return a jndi.properties object that is based on the
	 * properties found in ejava-jndi.properties that start with the provided
	 * prefix. This method is useful when examples are using different JNDI
	 * mechanisms and want to keep them separate by forming the jndi.properties
	 * in memory.
	 * @param prefix
	 * @return Properties object that can be used during a new InitialContext(env)
	 * @throws IOException
	 */
    public static Properties getJNDIProperties(String prefix) throws IOException {
    	InputStream is = null;
    	for (int i=0; is==null && i<PATHS.length; i++) {
    		logger.debug("trying: {}", PATHS[i]);
    		is = JNDIUtil.class.getResourceAsStream(PATHS[i]);
    	}
    	for (int i=0; is==null && i<PATHS.length; i++) {
    		logger.debug("trying loader for thread: {}", PATHS[i]);
    		is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATHS[i]);
    	}
    	
    	Properties env = new Properties();
    	if (is!=null) {
        	Properties props = new Properties();
        	props.load(is);
        	is.close();
        	
        	for (String key : props.stringPropertyNames()) {
        		String value = props.getProperty(key);
        		if (key.startsWith(prefix) && value != null && !value.isEmpty()) {
        			String name=key.substring(prefix.length(),key.length());
        			env.put(name, value);
        		}
        	}
    	} else {
    		logger.warn("unable to locate ejava-jndi.properties from classpath");
    	}
    	return env;
    }
	
    /**
     * Performs a JNDI lookup and will wait supplied number of seconds before 
     * giving up.
     * @param ctx
     * @param type
     * @param name
     * @param waitSecs
     * @return Object resolved from the lookup
     * @throws NamingException 
     */
    @SuppressWarnings("unchecked")
	public static <T> T lookup(Context ctx, Class<T> type, String name, int waitSecs) 
			throws NamingException {
    	logger.debug("looking up {}, wait={}", name, waitSecs);
    	
    	T object=null;
    	//wait increments should be at least 1sec
    	long interval=Math.max(waitSecs*1000/10, 1000);
    	for (int elapsed=0; elapsed<(waitSecs*1000); elapsed += interval) {
    		if (elapsed + interval < waitSecs*1000) {
	    		try {
					object = (T) ctx.lookup(name);
				} catch (Throwable ex) {
					logger.debug("error in jndi.lookup({})={}", name, ex);
					try { Thread.sleep(interval); } catch (Exception ex2) {}
				}
    		} else {
				object = (T) ctx.lookup(name);
    		}
    	}
    	logger.debug("object=" + object);
    	return object;
    }
		
	/**
     * Produces a debug string listing the JNDI contents of the current, default 
     * Context.
     * @return String describing contents of context
	 * @throws NamingException
	 */
    public String dump() throws NamingException {
        return dump(new InitialContext(),"");
    }

    /**
     * Produces a debug string listing the JNDI contents of the specified 
     * Context.
     * @param context
     * @param name
     * @return String describing contents of context
     */
    public String dump(Context context, String name) {
        StringBuilder text = new StringBuilder();
        if (name==null) { name = ""; }
        try {
            text.append("listing ").append(name);
            doDump(0, text, context, name);
        }
        catch (NamingException ex) {}
        return text.toString();
    }

	private void doDump(int level, StringBuilder text, Context context, String name) 
        throws NamingException {
        for (NamingEnumeration<NameClassPair> ne = context.list(name); ne.hasMore();) {
            NameClassPair ncp = (NameClassPair) ne.next();
            String objectName = ncp.getName();
            String className = ncp.getClassName();
            if (isContext(className)) {
            	text.append(getPad(level))
            	    .append("+")
            	    .append(objectName)
            	    .append(":")
            	    .append(className)
            	    .append('\n');
                doDump(level + 1, text, context, name + "/" + objectName);
            } else {
            	text.append(getPad(level))
	        	    .append("-")
	        	    .append(objectName)
	        	    .append(":")
	        	    .append(className)
	        	    .append('\n');
            }
        }
    }
    
	protected boolean isContext(String className) {
        try {
			Class<?> objectClass = Thread.currentThread().getContextClassLoader()
                    .loadClass(className);
            return Context.class.isAssignableFrom(objectClass);
        }
        catch (ClassNotFoundException ex) {
            //object is probably not a context, report as non-context
            return false;
        }
    }

    protected String getPad(int level) {
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < level; i++) {
            pad.append(" ");
        }
        return pad.toString();
    }
}
