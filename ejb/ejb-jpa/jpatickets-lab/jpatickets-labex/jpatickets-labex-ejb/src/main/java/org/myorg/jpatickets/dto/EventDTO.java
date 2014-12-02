package org.myorg.jpatickets.dto;

import java.io.Serializable;
import java.util.Date;

public class EventDTO implements Serializable {
    private static final long serialVersionUID = 4881952492348300544L;
    private int id;
    private String eventName;
    private String venueName;
    private Date startTime;
    private int numTickets;
    
    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
    
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public int getNumTickets() { return numTickets; }
    public void setNumTickets(int numTickets) {
        this.numTickets = numTickets;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EventDTO [id=").append(id)
            .append(", eventName=").append(eventName)
            .append(", venueName=").append(venueName)
            .append(", startTime=").append(startTime)
            .append(", numTickets=").append(numTickets)
            .append("]");
        return builder.toString();
    }
}
