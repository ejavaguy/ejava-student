package org.myorg.jpatickets.ejbclient;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;
import org.myorg.jpatickets.bl.TicketsFactory;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.ejb.VenueMgmtRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VenueMgmtIT {
    private static final Logger logger = LoggerFactory.getLogger(VenueMgmtIT.class);    
    private static final String VENUE_JNDINAME = System.getProperty("jndi.name.venuemgmt",
        "ejb:jpatickets-labsol-ear/jpatickets-labsol-ejb/VenueMgmtEJB!org.myorg.jpatickets.ejb.VenueMgmtRemote");
        //"ejb:jpatickets-labsol-ear/jpatickets-labsol-ejb/VenueMgmtEJB!"+VenueMgmtRemote.class.getName());
    
    private TicketsFactory tf = new TicketsFactory();    
    private VenueMgmtRemote venueMgmt;

    @Before
    public void setUp() throws Exception {
        InitialContext jndi = new InitialContext();
        logger.info("{} looking up {}", jndi.getEnvironment(), VENUE_JNDINAME);
        venueMgmt = (VenueMgmtRemote) jndi.lookup(VENUE_JNDINAME);
    }

    @Test
    public void venue() {
        Venue venue = tf.makeVenue();
        venue = venueMgmt.createVenue(venue, 1, 2, 3);
    }
}
