package org.myorg.jpatickets.ejbclient;

import static org.junit.Assert.*;
import static org.myorg.jpatickets.ejbclient.TicketsITFactory.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.myorg.jpatickets.bl.UnavailableException;
import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Ticket;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.dto.EventDTO;
import org.myorg.jpatickets.ejb.EventMgmtRemote;
import org.myorg.jpatickets.ejb.VenueMgmtRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventMgmtIT {
    private static final Logger logger = LoggerFactory.getLogger(EventMgmtIT.class);
    
    private TicketsITFactory tf = new TicketsITFactory();    
    private VenueMgmtRemote venueMgmt;
    private EventMgmtRemote eventMgmt;

    @Before
    public void setUp() throws Exception {
        venueMgmt = tf.lookup(VenueMgmtRemote.class, VENUE_JNDINAME);
        eventMgmt = tf.lookup(EventMgmtRemote.class, EVENT_JNDINAME);
        tf.cleanup();
    }
    
    @Test
    @Ignore
    public void eventLazy() throws UnavailableException {
        logger.info("*** eventLazy ***");
        Venue venue = venueMgmt.createVenue(tf.makeVenue(), 1, 2, 2);
        Event event = eventMgmt.createEvent(tf.makeEvent(), venue);
        
        event=eventMgmt.getEvent(event.getId());
        assertNotNull("null tickets for event", event.getTickets());
        try {
            assertTrue("no tickets for event", event.getTickets().size() > 0);
            fail("did not get expected lazy-load exception");
        } catch (Exception ex) {
            logger.info("caught expected lazy-load exception:" + ex);
        }
    }
    
    @Test
    @Ignore
    public void eventTouchedSome() throws UnavailableException {
        logger.info("*** eventTouchedSome ***");
        Venue venue = venueMgmt.createVenue(tf.makeVenue(), 1, 2, 2);
        Event event = eventMgmt.createEvent(tf.makeEvent(), venue);
        
        event=eventMgmt.getEventTouchedSome(event.getId());
        assertNotNull("null tickets for event", event.getTickets());
        assertTrue("no tickets for event", event.getTickets().size() > 0);
        for (Ticket t: event.getTickets()) {
            try {
                assertNotNull("no ticket price:" + t, t.getPrice());
                fail("did not get expected lazy-load exception");
            } catch (Exception ex) {
                logger.info("caught expected lazy-load exception:" + ex);
            }
        }
    }
    
    @Test
    @Ignore
    public void eventTouchedMore() throws UnavailableException {
        logger.info("*** eventTouchedMore ***");
        Venue venue = venueMgmt.createVenue(tf.makeVenue(), 1, 2, 2);
        Event event = eventMgmt.createEvent(tf.makeEvent(), venue);
        
        event=eventMgmt.getEventTouchedMore(event.getId());
        assertNotNull("null tickets for event", event.getTickets());
        assertTrue("no tickets for event", event.getTickets().size() > 0);
        for (Ticket t: event.getTickets()) {
            assertNotNull("no ticket price:" + t, t.getPrice());
            assertTrue("unexpected ticket price:" + t, t.getPrice().intValue() > 0);
            Seat s = t.getSeat();
            assertNotNull("null seat", s);
        }
    }
    
    @Test
    @Ignore
    public void eventFetchedSome() throws UnavailableException {
        logger.info("*** eventFetchedSome ***");
        Venue venue = venueMgmt.createVenue(tf.makeVenue(), 1, 2, 2);
        Event event = eventMgmt.createEvent(tf.makeEvent(), venue);
        
        event=eventMgmt.getEventFetchedSome(event.getId());
        assertNotNull("null tickets for event", event.getTickets());
        assertTrue("no tickets for event", event.getTickets().size() > 0);
        for (Ticket t: event.getTickets()) {
            try {
                assertNotNull("no ticket price:" + t, t.getPrice());
                fail("did not get expected lazy-load exception");
            } catch (Exception ex) {
                logger.info("caught expected lazy-load exception:" + ex);
            }
        }
    }
    
    @Test
    @Ignore
    public void eventFetchedMore() throws UnavailableException {
        logger.info("*** eventFetchedMore ***");
        Venue venue = venueMgmt.createVenue(tf.makeVenue(), 1, 2, 2);
        Event event = eventMgmt.createEvent(tf.makeEvent(), venue);
        
        event=eventMgmt.getEventFetchedMore(event.getId());
        assertNotNull("null tickets for event", event.getTickets());
        assertTrue("no tickets for event", event.getTickets().size() > 0);
        for (Ticket t: event.getTickets()) {
            assertNotNull("no ticket price:" + t, t.getPrice());
            assertTrue("unexpected ticket price:" + t, t.getPrice().intValue() > 0);
            Seat s = t.getSeat();
            assertNotNull("null seat", s);
        }
    }
    
    @Test
    @Ignore
    public void eventLazyDTO() throws UnavailableException {
        logger.info("*** eventLazyDTO ***");
        Venue venue = venueMgmt.createVenue(tf.makeVenue(), 1, 2, 2);
        Event event = tf.makeEvent();
        int eventId = eventMgmt.createEvent(tf.makeEvent(), venue).getId();
        
        EventDTO dto=eventMgmt.getEventLazyDTO(eventId);
        logger.debug("eventDTO={}", dto);
        assertEquals("unexpected eventName", event.getName(), dto.getEventName());
        assertEquals("unexpected venueName", venue.getName(), dto.getVenueName());
        assertEquals("unexpected startTime", event.getStartTime(), dto.getStartTime());
        assertTrue("no tickets for event", dto.getNumTickets() > 0);
    }
    
    @Test
    @Ignore
    public void eventFetchedDTO() throws UnavailableException {
        logger.info("*** eventFetchedDTO ***");
        Venue venue = venueMgmt.createVenue(tf.makeVenue(), 1, 2, 2);
        Event event = tf.makeEvent();
        int eventId = eventMgmt.createEvent(tf.makeEvent(), venue).getId();
        
        EventDTO dto=eventMgmt.getEventFetchedDTO(eventId);
        logger.debug("eventDTO={}", dto);
        assertEquals("unexpected eventName", event.getName(), dto.getEventName());
        assertEquals("unexpected venueName", venue.getName(), dto.getVenueName());
        assertEquals("unexpected startTime", event.getStartTime(), dto.getStartTime());
        assertTrue("no tickets for event", dto.getNumTickets() > 0);
    }
}
