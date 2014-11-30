package org.myorg.jpatickets.bo;

import java.io.Serializable;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="JPATICKET_SEAT")
@NamedQueries({
    @NamedQuery(name = "JPATicketSeat.getSeatsForVenue", 
            query = "select s from Seat s where s.venue = :venue")
})
public class Seat implements Serializable {
    @EmbeddedId
    private SeatPK pk;
    
    @MapsId("venueId")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="VENUE_ID")
    private Venue venue;
    
    protected Seat() {}
    public Seat(Venue venue, String section, int row, int position) {
        this.venue = venue;
        this.pk = new SeatPK(venue==null ? null : venue.getId(), section, row, position);
    }

    public Venue getVenue()    { return venue; }
    public String getVenueId() { return pk==null ? null : pk.getVenueId(); }
    public String getSection() { return pk==null ? null : pk.getSection(); }
    public int getRow()        { return pk==null ? 0 : pk.getRow(); }
    public int getPosition()   { return pk==null ? 0 : pk.getPosition(); }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getPosition();
        result = prime * result + getRow();
        result = prime * result + ((getSection() == null) ? 0 : getSection().hashCode());
        result = prime * result + ((getVenueId() == null) ? 0 : getVenueId().hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Seat other = (Seat) obj;
        return (getPosition() == other.getPosition()) &&
           (getRow() != other.getRow()) &&
           (getSection() == null ? other.getSection()==null : getSection().equals(other.getSection())) &&
           (getVenueId() ==null ? other.getVenueId()==null : venue.getId().equals(other.getVenueId()));
    }
    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Seat [venue=").append(getVenueId())
                .append(", section=").append(getSection())
                .append(", row=").append(getRow())
                .append(", position=").append(getPosition())
                .append("]");
        return builder.toString();
    }
}
