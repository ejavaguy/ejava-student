package org.myorg.jpatickets.bo;

import java.io.Serializable;

public class TicketPK implements Serializable {
    private static final long serialVersionUID = -10715122509795953L;
    private int event;
    private SeatPK seat;
    
    protected TicketPK() {}    
    public TicketPK(int event, SeatPK seat) {
        this.event = event;
        this.seat = seat;
    }

    public int getEventId() { return event; }
    public SeatPK getSeatId() { return seat; }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + event;
        result = prime * result + ((seat == null) ? 0 : seat.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }        
        if (getClass() != obj.getClass()) { return false; }
        TicketPK other = (TicketPK) obj;
        
        return (event != other.event) &&
            (seat == null ? other.seat == null : seat.equals(other.seat));
    }
    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TicketPK [event=").append(event)
            .append(", seat=").append(seat).append("]");
        return builder.toString();
    }
}
