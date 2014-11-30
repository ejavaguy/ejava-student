package org.myorg.jpatickets.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="JPATICKETS_EVENT")
@NamedQueries({
    @NamedQuery(name="JPATicketEvent.fetchEventTickets", 
        query="select e from Event e "
                + "join fetch e.tickets "
                + "where e.id=:eventId"),
    @NamedQuery(name="JPATicketEvent.fetchEventTicketsSeats", 
        query="select e from Event e "
                + "join fetch e.venue "
                + "join fetch e.tickets t "
                + "join fetch t.seat "
                + "where e.id=:eventId")
})
@NamedNativeQueries({
    @NamedNativeQuery(name="JPATicketEvent.fetchEventDTO",
            query="select event.EVENT_ID, event.EVENT_NAME, event.START_TIME, event.VENUE_ID, "
                    + "venue.NAME venueName, count(ticket.*) numTickets "
                    + "from JPATICKETS_EVENT event "
                    + "join JPATICKETS_VENUE venue on venue.VENUE_ID = event.VENUE_ID "
                    + "join JPATICKETS_TICKET ticket on ticket.EVENT_ID = event.EVENT_ID "
                    + "where event.EVENT_ID = :eventId "
                    + "group by event.EVENT_ID, event.EVENT_NAME, event.START_TIME, event.VENUE_ID, venue.NAME",
            resultSetMapping="JPATicketEvent.EventDTOMapping")
})
@SqlResultSetMappings({
    @SqlResultSetMapping(name="JPATicketEvent.EventDTOMapping",
            entities={
                @EntityResult(entityClass=Event.class)
                },
            columns={
                @ColumnResult(name="venueName", type=String.class),
                @ColumnResult(name="numTickets", type=Number.class)
                }
    )
})
public class Event implements Serializable {
    @Id @GeneratedValue
    @Column(name="EVENT_ID")
    private int id;
    
    @Column(name="EVENT_NAME", length=40, nullable=false)
    private String name;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="VENUE_ID", nullable=false, updatable=false)
    private Venue venue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="START_TIME")
    private Date startTime;
    
    @OneToMany(mappedBy="event", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Ticket> tickets;
    
    protected Event() { }
    public Event(Venue venue) { 
        this.venue=venue;
    }
    public Event(int id) { this.id=id; }

    public int getId() { return id; }
    public Venue getVenue() { return venue; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    public Event withName(String name) {
        setName(name);
        return this;
    }
    
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Event withStartTime(Date startTime) {
        setStartTime(startTime);
        return this;
    }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    public Event withTicket(Ticket ticket) {
        if (tickets==null) {
            tickets = new LinkedList<Ticket>();
        }
        if (ticket!=null) {
            tickets.add(ticket);
        }
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Event [id=").append(id)
                .append(", name=").append(name)
                .append(", venue=").append(venue==null ? null : venue.getName())
                .append(", startTime=").append(startTime)
                .append("]");
        return builder.toString();
    }
}
