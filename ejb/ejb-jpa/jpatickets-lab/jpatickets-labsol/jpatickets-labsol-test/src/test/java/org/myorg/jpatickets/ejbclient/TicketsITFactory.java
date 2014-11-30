package org.myorg.jpatickets.ejbclient;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.myorg.jpatickets.bl.TicketsFactory;
import org.myorg.jpatickets.ejb.EventMgmtRemote;
import org.myorg.jpatickets.ejb.TicketsInitRemote;
import org.myorg.jpatickets.ejb.VenueMgmtRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketsITFactory extends TicketsFactory {
    private static final Logger logger = LoggerFactory.getLogger(TicketsITFactory.class);
    
    public static final String INIT_JNDINAME = System.getProperty("jndi.name.ticketsinit",
            "ejb:jpatickets-labsol-ear/jpatickets-labsol-ejb/TicketsInitEJB!"+TicketsInitRemote.class.getName());
    public static final String VENUE_JNDINAME = System.getProperty("jndi.name.venuemgmt",
            "ejb:jpatickets-labsol-ear/jpatickets-labsol-ejb/VenueMgmtEJB!"+VenueMgmtRemote.class.getName());
    public static final String WEBIMPORTED_VENUE_JNDINAME = System.getProperty("jndi.name.web-imported.venuemgmt",
            "ejb:/jpatickets-labsol-war/VenueMgmtEJB!"+VenueMgmtRemote.class.getName());
    public static final String WEBVENUE_JNDINAME = System.getProperty("jndi.name.web-venuemgmt",
            "ejb:/jpatickets-labsol-war/WebVenueMgmtEJB!"+VenueMgmtRemote.class.getName());
    public static final String EVENT_JNDINAME = System.getProperty("jndi.name.eventmgmt",
            "ejb:jpatickets-labsol-ear/jpatickets-labsol-ejb/EventMgmtEJB!"+EventMgmtRemote.class.getName());
    
    @SuppressWarnings("unchecked")
    public <T> T lookup(Class<T> clazz, String jndiName) throws NamingException {        
        logger.debug("looking up {}", jndiName);
        return (T) new InitialContext().lookup(jndiName);
    }
    
    public void cleanup() throws NamingException {
        lookup(TicketsInitRemote.class,INIT_JNDINAME).resetDB();
    }

}
