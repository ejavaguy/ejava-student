package ejava.examples.jndidemo.ejb;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.jndidemo.Scheduler;
import ejava.util.jndi.JNDIUtil;

public abstract class SchedulerBase implements Scheduler {
    protected Log log = LogFactory.getLog(getClass());
    protected SessionContext ctx;
    
    protected abstract void setSessionContext(SessionContext ctx);
    
    public String getJndiProperty(String name) {
        Object object=null;
        try {
            InitialContext jndi = new InitialContext();
            object = jndi.lookup(name);
        }
        catch (NamingException ex) {
            object = ex;
        }        
        log.debug("jndi: " + name + "=" + object);

        return (object != null) ? object.toString() : null;            
    }

    public String getCtxProperty(String name) {
        Object object=null;
        try {
            object = ctx.lookup(name);
        }
        catch (RuntimeException ex) {
            object = ex;
        }        
        log.debug("enc: " + name + "=" + object);
        return (object != null) ? object.toString() : null;            
    }
    
    @Override
    public String getEnv() {
    	try {
			String result=new JNDIUtil().dump(new InitialContext(), "java:comp/env");
			log.debug("java:comp/env=" + result);
			return result;
		} catch (NamingException ex) {
			StringWriter sw = new StringWriter(); 
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			log.error(pw.toString());
			return pw.toString();
		}
    }
}
