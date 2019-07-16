package ejava.ejb.examples.encconfig.ejb;

import java.util.ArrayList;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ejb.examples.encconfig.dto.NCPair;

/**
 * This EJB provides server-side lookups of various names using a JNDI InitialContext
 * and SessionContext.
 */
public class JNDIReader implements JNDIReaderRemote {
	private static final Logger log = LoggerFactory.getLogger(JNDIReader.class);
	private @Resource SessionContext ctx;
	protected Context jndi;
	
	@PostConstruct
	public void init() {
		try {
			jndi=new InitialContext();
		} catch (NamingException ex) {
			ex.printStackTrace();
			throw new EJBException("error instantiating InitialContext:" + ex);
		}
	}
	
	@PreDestroy
	public void shutdown() {
		if (jndi!=null) {
			try { jndi.close(); } 
			catch (NamingException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	protected void verify(Context jndi, String area, String base, Object...object) 
			throws NamingException {
		for (int i=0; i<object.length; i++) {
			log.info("{}{}={}", base, i+1, object[i]);
			assert(object[i] !=null) : String.format("%s%s was null", base,i+1);
		}
		String name = String.format("%s/%s2", area, base);
		String jndiName = "java:comp/env/" + name;
		log.info("jndi.lookup({})", jndiName);
		Object jndiResult = jndi.lookup(jndiName);		
		log.info("InitialContext().lookup({})={}", jndiName, jndiResult);
		log.info("ctx.lookup({})", name);
		Object ctxResult = ctx.lookup(name);
		log.info("ctx.lookup({})={}", name, ctxResult);
		assert(jndiResult !=null) : String.format("jndi lookup for %s was null", jndiName);
		assert(ctxResult !=null) : String.format("ctx lookup for %s was null", name);
	}
	
	
	protected Object listContext(String name, Object result) {
		try {
			if (result instanceof Context) {
				List<NCPair> list = new ArrayList<NCPair>();
				for (NamingEnumeration<NameClassPair> ne=((Context)result).list(name);ne.hasMoreElements();) {
					NameClassPair ncp = ne.next();
					NCPair pair = new NCPair(ncp.getName(), ncp.getClassName());
					list.add(pair);
					log.debug("found {}", pair);
				}
				result = list;
			}
			return result;
		} catch (NamingException ex) {
			return ex.toString();
		}
	}

	@Override
	public Object listJNDI(String name) {
		try {
			log.debug("InitialContext().list:" + name);
			Object result = jndi.lookup(name);
			result = listContext(name, result);
			log.info("result:" + result);
			return result;
		} catch (NamingException ex) {
			return ex.toString();
		}
	}
	
	@Override
	public String lookupJNDI(String name) {
		try {
			log.debug("InitialContext().lookup:" + name);
			Object object = jndi.lookup(name);
			log.info("found:" + object);
			return object==null ? null : object.toString();
		} catch (NamingException ex) {
			log.info(String.format("jndi.lookup(%s)", name) + ex);
			return null;
		}
	}

	@Override
	public Object listSessionContext(String name) {
		try {
			log.debug("SessionContext.list:" + name);
			Object result = ctx.lookup(name);
			result = listContext(name, result);
			log.info("result:" + result);
			return result;
		} catch (Exception ex) {
			log.debug("error listing name:" + name, ex);
			return ex.toString();
		}
	}
	
	@Override
	public String lookupSessionContext(String name) {
		try {
			log.debug("SessionContext.lookup:" + name);
			Object object = ctx.lookup(name);
			log.info("found:" + object);
			return object==null ? null : object.toString();
		} catch (Exception ex) {
			log.info(String.format("ctx.lookup(%s)", name) + ex);
			return null;
		}
	}

}
