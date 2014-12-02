package org.myorg.jpatickets.ejbclient;

import static org.junit.Assert.assertNotNull;
import static org.myorg.jpatickets.ejbclient.TicketsITFactory.*;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.ejb.VenueMgmtRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VenueMgmtIT {
    private static final Logger logger = LoggerFactory.getLogger(VenueMgmtIT.class);    
    
    private TicketsITFactory tf = new TicketsITFactory();    

    @Before
    public void setUp() throws Exception {
        tf.cleanup();
    }
    
    @Test
    @Ignore
    public void venueEAR() throws NamingException {
        logger.info("*** venueEAR ***");
        
        VenueMgmtRemote venueMgmt=tf.lookup(VenueMgmtRemote.class, VENUE_JNDINAME);
        Venue venue = tf.makeVenue();
        venueMgmt.createVenue(venue, 1, 2, 3);
        
        assertNotNull("could not locate venue:" + venue.getId(), venueMgmt.getVenue(venue.getId()));
    }
    
    @Test
    @Ignore
    public void venueImportedEJB() throws NamingException {
        logger.info("*** venueImportedEJB ***");
        
        VenueMgmtRemote venueMgmt=tf.lookup(VenueMgmtRemote.class, WEBIMPORTED_VENUE_JNDINAME);
        Venue venue = tf.makeVenue();
        venueMgmt.createVenue(venue, 1, 2, 3);
        
        assertNotNull("could not locate venue:" + venue.getId(), venueMgmt.getVenue(venue.getId()));
    }
    
    @Test 
    @Ignore
    public void venueWAR() throws NamingException {
        logger.info("*** venueWAR ***");
        
        VenueMgmtRemote venueMgmt=tf.lookup(VenueMgmtRemote.class, WEBVENUE_JNDINAME);
        Venue venue = tf.makeVenue();
        venueMgmt.createVenue(venue, 1, 2, 3);
        
        assertNotNull("could not locate venue:" + venue.getId(), venueMgmt.getVenue(venue.getId()));
    }
}
