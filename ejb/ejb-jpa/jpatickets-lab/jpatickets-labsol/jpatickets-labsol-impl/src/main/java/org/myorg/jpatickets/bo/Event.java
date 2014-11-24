package org.myorg.jpatickets.bo;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="JPATICKETS_EVENT")
public class Event {
    @Id @GeneratedValue
    @Column(name="EVENT_ID")
    private int id;
    
    @Column(name="EVENT_NAME", length=40, nullable=false)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="START_TIME")
    private Date startTime;
    
    @OneToMany(mappedBy="event", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Ticket> tickets;
    
    public Event() { }
    public Event(int id) { this.id=id; }

    public int getId() { return id; }

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
                .append(", startTime=").append(startTime)
                .append("]");
        return builder.toString();
    }
}
