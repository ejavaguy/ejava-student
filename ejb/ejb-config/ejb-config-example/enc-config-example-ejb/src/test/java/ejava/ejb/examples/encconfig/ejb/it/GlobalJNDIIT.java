package ejava.ejb.examples.encconfig.ejb.it;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalJNDIIT {
	private static final Logger log = LoggerFactory.getLogger(GlobalJNDIIT.class);
	
	
	@Test
	public void test() throws NamingException {
		Context jndi = new InitialContext();
		try {
			jndi=new InitialContext();
			log.info(JNDIHelper.listJNDI(jndi, new StringBuilder(), "", null).toString());
		} finally {
			if (jndi != null) {
//				jndi.close();
			}
		}
	}

}
