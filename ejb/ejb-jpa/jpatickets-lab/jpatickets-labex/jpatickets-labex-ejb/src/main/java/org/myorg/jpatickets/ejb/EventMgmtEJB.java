package org.myorg.jpatickets.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.myorg.jpatickets.bl.EventMgmt;
import org.myorg.jpatickets.bl.EventMgmtImpl;
import org.myorg.jpatickets.bl.UnavailableException;
import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Ticket;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.dao.EventMgmtDAO;
import org.myorg.jpatickets.dao.EventMgmtDAOImpl;
import org.myorg.jpatickets.dao.VenueDAOImpl;
import org.myorg.jpatickets.dto.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class EventMgmtEJB implements EventMgmtRemote {
    private static final Logger logger = LoggerFactory.getLogger(EventMgmtEJB.class);
    
    //@PersistenceContext(unitName="jpatickets-labex")
    private EntityManager em;
    
    private EventMgmtDAO edao;
    private EventMgmt eventMgmt;
    
    @PostConstruct
    public void init() {
        logger.debug("*** EventMgmtEJB:init({}) ***", super.hashCode());
        
        edao = new EventMgmtDAOImpl();
        ((EventMgmtDAOImpl)edao).setEntityManager(em);
        VenueDAOImpl vdao = new VenueDAOImpl();
            vdao.setEntityManager(em);
        eventMgmt = new EventMgmtImpl();
        ((EventMgmtImpl)eventMgmt).setEventDAO(edao);
        ((EventMgmtImpl)eventMgmt).setVenueDAO(vdao);
    }
    
    @PreDestroy
    public void destroy() {
        logger.debug("*** EventMgmtEJB:destroy({}) ***", super.hashCode()); 
    }
    

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Event createEvent(Event event, Venue venue) throws UnavailableException {
        return eventMgmt.createEvent(event, venue);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Event getEvent(int id) {
        logger.debug("getEvent({})", id);
        return eventMgmt.getEvent(id);
    }
    
    @Override
    public Event getEventCleansed(int id) {
        logger.debug("getCleansedEvent({})", id);
        Event event = getEvent(id);
        return toCleansed(event);
    }
    
    private Event toCleansed(Event bo) {
        Event pojo = new Event(bo.getId());
        pojo.setName(bo.getName());
        pojo.setStartTime(bo.getStartTime());
        List<Ticket> tickets = new ArrayList<Ticket>(bo.getTickets().size());
        for (Ticket t: bo.getTickets()) {
            toCleansed(t, pojo);
        }
        pojo.setTickets(tickets);
        return pojo;
    }
    
    private Ticket toCleansed(Ticket bo, Event event) {
        //example cleansing is stopping here for the example
        Ticket pojo = new Ticket(event, bo.getSeat());
        pojo.setPrice(bo.getPrice());
        pojo.setSold(bo.isSold());
        return pojo;
    }
    
    @Override
    public Event getEventTouchedSome(int id) {
        logger.debug("getEventTouchedSome({})", id);
        Event event = getEvent(id);
        //touch the ticket collection to load tickets prior to marshaling back
        event.getTickets().size();
        return event;
    }
    
    @Override
    public Event getEventTouchedMore(int id) {
        logger.debug("getEventTouchedMore({})", id);
        Event event = getEvent(id);
        //touch ticket collection and all seats to load both prior to marshaling back
        event.getTickets().size();
        event.getVenue().getName();
        for (Ticket t: event.getTickets()) {
            t.getSeat().getPosition();
        }
        return event;
    }
    
    @Override
    public Event getEventFetchedSome(int id) {
        logger.debug("getEventFetchedSome({})", id);
        Event event = eventMgmt.fetchEventTickets(id);
        return event;
    }
    
    @Override
    public Event getEventFetchedMore(int id) {
        logger.debug("getEventFetchedMore({})", id);
        Event event = eventMgmt.fetchEventTicketsSeats(id);
        return event;
    }
    
    @Override
    public EventDTO getEventLazyDTO(int id) {
        logger.debug("getEventLazyDTO({})", id);
        Event event = eventMgmt.getEvent(id);
        return toEventDTO(event);
    }
    
    private EventDTO toEventDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setEventName(event.getName());
        dto.setVenueName(event.getVenue().getName());
        dto.setStartTime(event.getStartTime());
        dto.setNumTickets(event.getTickets().size());
        return dto;
    }
    
    @Override
    public EventDTO getEventFetchedDTO(int eventId) {
        logger.debug("getEventFetchedDTO({})", eventId);
        Map<String, Object> dtoData = edao.fetchEventDTOData(eventId);
        return toEventDTO(dtoData);
    }
    
    private EventDTO toEventDTO(Map<String, Object> dtoData) {
        EventDTO dto = new EventDTO();
        Event event = (Event) dtoData.get(EventMgmtDAO.EVENT);
        String venueName = (String) dtoData.get(EventMgmtDAO.VENUE_NAME);
        int numTickets = (Integer) dtoData.get(EventMgmtDAO.NUM_TICKETS);
        dto.setId(event.getId());
        dto.setEventName(event.getName());
        dto.setStartTime(event.getStartTime());
        dto.setVenueName(venueName);
        dto.setNumTickets(numTickets);
        return dto;
    }
    
}
