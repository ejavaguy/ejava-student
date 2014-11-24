package org.myorg.jpatickets.bl;

import static org.junit.Assert.*;


import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.myorg.jpatickets.bo.Address;
import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Seat;
import org.myorg.jpatickets.bo.Ticket;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.dao.EventMgmtDAOImpl;
import org.myorg.jpatickets.dao.VenueDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketsTest {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(TicketsTest.class);
    private static final String PU_NAME="jpatickets-test";
    private static EntityManagerFactory emf;
    private EntityManager em;
    private VenueMgmt venueMgmt;
    private EventMgmt eventMgmt;
    
    @BeforeClass
    public static void setupClass() {
        emf = Persistence.createEntityManagerFactory(PU_NAME);
    }
    
    @Before 
    public void setUp() {
        em = emf.createEntityManager();
        venueMgmt = new VenueMgmtImpl();
        VenueDAOImpl vdao = new VenueDAOImpl();
        vdao.setEntityManager(em);
        ((VenueMgmtImpl)venueMgmt).setDao(vdao);
        
        EventMgmtDAOImpl edao = new EventMgmtDAOImpl();
        edao.setEntityManager(em);
        eventMgmt = new EventMgmtImpl();
        ((EventMgmtImpl)eventMgmt).setEventDAO(edao);
        ((EventMgmtImpl)eventMgmt).setVenueDAO(vdao);
        
        cleanup();
        
        em.getTransaction().begin();
    }
    
    @After
    public void tearDown() {
        if (em!=null) {
            if (em.getTransaction().isActive()) {
                if (em.getTransaction().getRollbackOnly()) {
                    em.getTransaction().rollback();
                } else {
                    em.getTransaction().commit();
                }
            }
            em.close();
            em=null;
        }
    }
    
    private void cleanup() {
        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.schema-generation.database.action", "drop-and-create");
        Persistence.generateSchema(PU_NAME, props);
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (emf!=null) {
            emf.close(); 
            emf=null;
        }
    }
    
    private Venue makeVenue() {
        return new Venue("VZC")
        .withName("Verizon Center")
        .withAddress(new Address()
            .withStreet("601 F Street NW")
            .withCity("Washington")
            .withState("DC")
            .withZipCode(20004));
    }
    
    private Event makeEvent() {
        return new Event()
            .withName("FLEETWOOD MAC ON WITH THE SHOW TOUR")
            .withStartTime(new GregorianCalendar(2015, Calendar.JANUARY, 30, 20, 0, 0).getTime());
    }
    
    @Test
    public void venue() {
        Venue venue = makeVenue();
        venueMgmt.createVenue(venue, 1, 2, 3);
        em.flush(); em.clear();
        
        assertNotNull("could not locate venue:" + venue.getId(), venueMgmt.getVenue(venue.getId()));
    }
    
    @Test
    public void event() throws UnavailableException {
        Venue venue = venueMgmt.createVenue(makeVenue(), 2, 3, 4);
        Event event = eventMgmt.createEvent(makeEvent(), venue);
        em.flush(); em.clear();
        
        event=eventMgmt.getEvent(event.getId());
        assertTrue("no tickets for event", event.getTickets()!=null && event.getTickets().size() > 0);
    }
    
    @Test
    public void ticket() throws UnavailableException {
        Venue venue = venueMgmt.createVenue(makeVenue(), 2, 3, 4);
        Event event = eventMgmt.createEvent(makeEvent(), venue);
        List<Seat> seats = eventMgmt.findSeats(event, null, 0, null, 0, 1);
        assertTrue("no seats available", seats.size() > 0);
        List<Ticket> tickets = eventMgmt.reserveSeats(event, Arrays.asList(seats.get(0)));
        assertEquals("unexpected number of tickets", 1, tickets.size());
        assertTrue("ticket not sold", tickets.get(0).isSold());
    }

}
