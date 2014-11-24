package org.myorg.jpatickets.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SeatPK implements Serializable {
    private static final long serialVersionUID = -5824145862784177861L;
    //mapped to FK
    private String venueId;
    @Column(name="SECTION", length=6)
    private String section;
    @Column(name="ROW")
    private int row;
    @Column(name="POSTION")
    private int position;
    
    protected SeatPK() {}

    public SeatPK(String venueId, String section, int row, int position) {
        this.venueId = venueId;
        this.section = section;
        this.row = row;
        this.position = position;
    }
    
    public String getVenueId() { return venueId; }
    public String getSection() { return section; }
    public int getRow() { return row; }
    public int getPosition() { return position; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + position;
        result = prime * result + row;
        result = prime * result + ((section == null) ? 0 : section.hashCode());
        result = prime * result + ((venueId == null) ? 0 : venueId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        SeatPK other = (SeatPK) obj;
        return (position == other.position) &&
            (row == other.row) &&
            (section==null ? other.section==null : section.equals(other.section))  &&
            (venueId==null ? other.venueId==null : venueId.equals(other.venueId));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SeatPK [venueId=").append(venueId)
            .append(", section=").append(section)
            .append(", row=").append(row)
            .append(", position=").append(position)
            .append("]");
        return builder.toString();
    }

    
}
