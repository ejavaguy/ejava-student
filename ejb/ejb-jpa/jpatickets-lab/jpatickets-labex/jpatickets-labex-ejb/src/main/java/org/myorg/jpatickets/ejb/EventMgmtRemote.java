package org.myorg.jpatickets.ejb;

import javax.ejb.Remote;

import org.myorg.jpatickets.bl.UnavailableException;
import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.dto.EventDTO;

@Remote
public interface EventMgmtRemote {
    Event createEvent(Event event, Venue venue) throws UnavailableException;
    Event getEvent(int id);
    Event getEventTouchedSome(int id);
    Event getEventTouchedMore(int id);
    Event getEventFetchedSome(int id);
    Event getEventFetchedMore(int id);
    EventDTO getEventLazyDTO(int id);
    EventDTO getEventFetchedDTO(int eventId);
}
