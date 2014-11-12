package info.ejava.examples.secureping.ejb;

import info.ejava.examples.secureping.ejb.SecurePingRemote;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This session bean allows all methods to be invoked and then performs 
 * the matching operation on SecurePingEJB using a run-as with an admin role.
 */
@Stateless
@PermitAll
@RunAs("admin")
public class SecurePingClientEJB 
    implements SecurePingClientRemote, SecurePingClientLocal {
    private static final Logger logger = LoggerFactory.getLogger(SecurePingClientEJB.class);
    
    @Resource
    SessionContext ctx;
   
    @EJB(lookup="ejb:securePingEAR/securePingEJB/SecurePingEJB!info.ejava.examples.secureping.ejb.SecurePingRemote")
    SecurePingRemote securePingServer;
    
    @PostConstruct
    public void init() {
        logger.debug("*** SecurePingClientEJB initializing ***");
        logger.debug("securePingServer=" + securePingServer);
    }
    
    /**
     * Return what this EJB's container thinks about the caller.
     */
    private String getInfo(String prefix) {
        StringBuilder text = new StringBuilder();
        text.append("securePingClient called " + prefix);
        try {
            text.append(", principal=" + ctx.getCallerPrincipal().getName());
            text.append(", isUser=" + ctx.isCallerInRole("user"));
            text.append(", isAdmin=" + ctx.isCallerInRole("admin"));
            text.append(", isInternalRole=" + 
                    ctx.isCallerInRole("internalRole"));
        }
        catch (Throwable ex) {
            logger.debug("error calling session context:", ex);
            text.append(", error calling Session Context:" + ex);
        }
        String result = text.toString();
        logger.debug(result);
        return result;        
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingAll() {
        return getInfo("pingAll") + ":\nsecurePing=" + securePingServer.pingAll();
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingUser() {
        return getInfo("pingUser") + ":\nsecurePing=" + securePingServer.pingUser();
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingAdmin() {        
        return getInfo("pingAdmin") + ":\nsecurePing=" + securePingServer.pingAdmin();
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingExcluded() {
        return getInfo("pingExcluded") + ":\nsecurePing=" + securePingServer.pingExcluded();
    }
    
    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as. Most of the details are written to the log since the return
     * type here is a simple boolean.
     */
    public boolean isCallerInRole(String role) {
        boolean result = ctx.isCallerInRole(role);
        logger.debug("securePingClient.user=" + ctx.getCallerPrincipal().getName() + 
                ", isCallerInRole(" + role + ")=" + result + 
                "\n:securePing=" + securePingServer.isCallerInRole(role));  
        return result;
    }
}
