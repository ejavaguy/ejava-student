package ejava.ejb.examples.encconfig.ejb.it;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ejb.examples.encconfig.ejb.JNDIReaderRemote;
import ejava.util.jndi.JNDIUtil;

public class GlobalJNDIIT {
	private static final Logger log = LoggerFactory.getLogger(GlobalJNDIIT.class);
    private static final String ejbJNDI = System.getProperty("jndi.name",
       "ejb:/enc-config-example-ejb/AnnotatedEJB!ejava.ejb.examples.encconfig.ejb.JNDIReaderRemote");
    
    @BeforeClass
    public static void setUpClass() {
//        log.info("sleeping 10secs");
//        try { Thread.sleep(10000); } catch (Exception ex) {}
    }
		
	@Test
	public void test() throws NamingException {
		Context jndi = new InitialContext();
		try {
			jndi=new InitialContext();
			log.info(JNDIHelper.listJNDI(jndi, new StringBuilder(), "", null).toString());
	        JNDIUtil.lookup(jndi, JNDIReaderRemote.class, ejbJNDI, 30);
		} finally {
			if (jndi != null) {
//				jndi.close();
			}
		}
	}

}
