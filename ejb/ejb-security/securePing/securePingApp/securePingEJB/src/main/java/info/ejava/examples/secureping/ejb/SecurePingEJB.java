package info.ejava.examples.secureping.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This session bean provides several methods; each of which will require
 * some type of role associated with the user in order to successfully 
 * invoke them.
 */
@Stateless(name="SecurePingEJB")
public class SecurePingEJB 
    implements SecurePingRemote, SecurePingLocal {
    private static final Logger logger = LoggerFactory.getLogger(SecurePingEJB.class);
    
    @Resource
    SessionContext ctx;
    
    @PostConstruct
    public void init() {
        logger.debug("*** SecurePingEJB initializing ***");    
    }
    
    /**
     * This method creates a status string based on security information
     * obtained from the SessionContext.
     */
    private String getInfo(String prefix) {
        StringBuilder text = new StringBuilder();
        text.append("called " + prefix);
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
     * This method is permitted to be called by anyone.
     */
    @PermitAll
    public String pingAll() {
        return getInfo("pingAll");
    }

    /**
     * Callers of this method must have the "user" role.
     */
    @RolesAllowed({"user"})
    public String pingUser() {
        return getInfo("pingUser");
    }

    /**
     * Callers of this method must have the "admin" role.
     */
    @RolesAllowed({"admin"})
    public String pingAdmin() {        
        return getInfo("pingAdmin");
    }

    /**
     * No one should be allowed to call this method.
     */
    @DenyAll
    public String pingExcluded() {
        return getInfo("pingExcluded");
    }
    
    /**
     * This method allows the RMI Test to check whether the current subject
     * has a specific role. This type of method would normally be used 
     * within an EJB to perform object-level access control.
     */
    @PermitAll
    public boolean isCallerInRole(String role) {
        boolean result = ctx.isCallerInRole(role);
        logger.debug("user=" + ctx.getCallerPrincipal().getName() + 
                ", isCallerInRole(" + role + ")=" + result);  
        return result;
    }
    
    @PermitAll
    public String getPrincipal() {
        String name= ctx.getCallerPrincipal().getName();
        logger.debug("getPrincipal(), name=" + name);
    	return name;
    }
}
