package org.myorg.jpatickets.bl;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.myorg.jpatickets.bo.Address;
import org.myorg.jpatickets.bo.Event;
import org.myorg.jpatickets.bo.Venue;

public class TicketsFactory {
    
    public Venue makeVenue() {
        return new Venue("VZC")
        .withName("Verizon Center")
        .withAddress(new Address()
            .withStreet("601 F Street NW")
            .withCity("Washington")
            .withState("DC")
            .withZipCode(20004));
    }
    
    public Event makeEvent() {
        return new Event()
            .withName("FLEETWOOD MAC ON WITH THE SHOW TOUR")
            .withStartTime(new GregorianCalendar(2015, Calendar.JANUARY, 30, 20, 0, 0).getTime());
    }

}
