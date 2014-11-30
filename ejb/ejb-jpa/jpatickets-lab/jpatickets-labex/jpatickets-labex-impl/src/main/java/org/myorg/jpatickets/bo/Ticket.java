package org.myorg.jpatickets.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="JPATICKETS_TICKET")
@NamedQueries({
    @NamedQuery(name="JPATicketTicket.findTickets", 
        query="select t from Ticket t where t.seat in :seats")
})
@IdClass(TicketPK.class)
public class Ticket implements Serializable {
    @Id
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="EVENT_ID")
    private Event event;
    
    @Id
    @OneToOne(
            //fetch=FetchType.EAGER,
            fetch=FetchType.LAZY,
            optional=false)
    @JoinColumns({
        @JoinColumn(name="VENUE_ID"),
        @JoinColumn(name="SECTION"),
        @JoinColumn(name="ROW"),
        @JoinColumn(name="POSITION"),
    })
    private Seat seat;
    
    @Column(name="PRICE", precision=7, scale=2)
    private BigDecimal price;
    
    @Column(name="SOLD", nullable=false)
    private boolean sold;

    protected Ticket() {}    
    public Ticket(Event event, Seat seat) {
        this.event = event;
        this.seat = seat;
    }

    public Event getEvent()   { return event; }
    public Seat getSeat()     { return seat; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public boolean isSold() { return sold; }
    public void setSold(boolean sold) {
        this.sold = sold;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Ticket [event=").append(event)
                .append(", seat=").append(seat)
                .append(", price=").append(price)
                .append(", sold=").append(sold)
                .append("]");
        return builder.toString();
    }
    
    
}
