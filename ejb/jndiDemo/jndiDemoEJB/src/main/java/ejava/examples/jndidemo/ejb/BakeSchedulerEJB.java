package ejava.examples.jndidemo.ejb;

import javax.annotation.PostConstruct;import javax.annotation.Resource;

import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.sql.DataSource;

import ejava.util.jndi.JNDIUtil;

/**
 * This class is primarily an example of configuring an EJB through 
 * @Annotations. There is no external ejb-jar.xml deployment descriptor 
 * entries for this EJB.
 * 
 * @author jcstaff
 *
 */
@Stateful(name="BakeScheduler")
/**
 * The PersistenceContext placed at this level is not common because from the
 * Java class -- we can directly inject into the variables we want. However,
 * this does simulate what is beiong done from ejb-jar.xml by injecting the 
 * persistence context into the ENC so that it can be looked up within the 
 * component using JNDI.
 */
@PersistenceContext(unitName="jndidemo",
		name="persistence/jndidemo",
		type=PersistenceContextType.EXTENDED)
public class BakeSchedulerEJB 
    extends SchedulerBase implements BakeSchedulerRemote {

    public String getName() { return "BakeSchedulerEJB"; }
    
    /*
     * This injects a enity manager into the class variable. Since this 
     * happens to be a stateless session bean -- an EXTENDED context is 
     * chosen over a TX-scoped.
     */
    @PersistenceContext(
    		unitName="jndidemo",    		
    		type=PersistenceContextType.EXTENDED)
    private EntityManager em;
    
    /**
     * This instance of an entity manager is being taken out of the JNDI
     * tree configured at the top of the class. This would be made available
     * to any POJO called by the EJB. 
     */
    @Resource(name="persistence/jndidemo")
    private EntityManager em2;

    /*
     * This declaration obtains a reference to the SQL DataSource in the '
     * global JNDI tree and initializes ds to that value
     */
    @Resource(mappedName="java:jboss/datasources/ExampleDS")
    private DataSource ds;
    
    /*
     * This declaration will cause the container to inject a SessionContext
     * into the EJB at startup.
     */
    @Resource
    protected void setSessionContext(SessionContext ctx) {
        super.ctx = ctx;
    }
    
    /* 
     * We will manually assign this value using a JNDI lookup inside the 
     * @PostConstruct method
     */
    protected CookLocal cook; 

    /*
     * This reference will be injected by the container based on the data
     * type declared. The resolved EJB also gets placed in the ejb/cook ENC
     * name so that it can be looked up by code using the JNDI tree directly.
     */
    @EJB(name="ejb/cook")
    protected CookLocal cook2; 

    /*
     * This won't resolve to anything since this example does not use an 
     * external deployment descriptor to give us a value.
     */
    @Resource(name="vals/message")
    String message;

    @PostConstruct
    public void init() {        
        log.info("******************* BakeScheduler Created ******************");
        log.debug("ctx=" + ctx);
        log.debug("em=" + em);
        log.debug("em2=" + em2);
        log.debug("ds=" + ds);
        //log.debug("persistence/jndidemo=" + ctx.lookup("persistence/jndidemo"));
        log.debug("message=" + message);
        log.debug("cook=" + cook);  //this will be null at this point
        log.debug("cook2=" + cook2);
        log.debug("ejb/cook=" + ctx.lookup("ejb/cook"));
        cook = (CookLocal)ctx.lookup("ejb/cook");
        try { 
        	log.debug(new JNDIUtil().dump(new InitialContext(), "java:comp/env"));
        } catch (NamingException ex) { log.error("" + ex); }
    }
}
