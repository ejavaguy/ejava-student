package org.myorg.basicejb.warejb;

import static org.junit.Assert.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.myorg.basicejb.ejb.ReservationRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationIT {
    private static final Logger logger = LoggerFactory.getLogger(ReservationIT.class);
    private static final String reservationJNDI = System.getProperty("jndi.name.reservation",
            "ejb:/basicejb-war/ReservationEJB!"+ReservationRemote.class.getName()); 
    private InitialContext jndi;
    private ReservationRemote reservationist; 
    
    @Before
    public void setUp() throws NamingException {
        assertNotNull("jndi.name.reservation not supplied", reservationJNDI);

        logger.debug("getting jndi initial context");
        jndi=new InitialContext();
        logger.debug("jndi={}", jndi.getEnvironment());
        jndi.lookup("jms");
        
        logger.debug("jndi name:{}", reservationJNDI);
        reservationist = (ReservationRemote) jndi.lookup(reservationJNDI);
        logger.debug("reservationist={}", reservationist);
    }
    
    @Test
    public void testPing() throws NamingException {
        logger.info("*** testPing ***");
        reservationist.ping();
    }
}
