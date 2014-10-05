package org.myorg.basicejb.ejb;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationTest {
    private static final Logger logger = LoggerFactory.getLogger(ReservationTest.class);
    
    ReservationRemote reservatist;
    
    @Before
    public void setUp() {
        reservatist=new ReservationEJB();
    }
    
    @Test
    public void testPing() {
        logger.info("*** testPing ***");
        reservatist.ping();
    }
}