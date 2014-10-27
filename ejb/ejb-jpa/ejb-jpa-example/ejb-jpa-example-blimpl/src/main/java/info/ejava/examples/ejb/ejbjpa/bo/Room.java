package info.ejava.examples.ejb.ejbjpa.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="EJBJPA_ROOM")
@NamedQueries({
    @NamedQuery(name="Room.findRoomByGuest", 
            query="select r from Room r where r.occupant=:guest")
})
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ROOM_NUMBER")
    private int number;
    
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="FLOOR_ID")
    private Floor floor;
    
    @OneToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name="OCCUPANT_ID")
    private Guest occupant;

    public Room() {}
    public Room(Floor floor, int number) {
        this.floor = floor;
        this.number = number;
    }
    
    public int getNumber() { return number; }
    public Floor getFloor() { return floor; }

    public Guest getOccupant() { return occupant; }
    public void setOccupant(Guest occupant) {
        this.occupant = occupant;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Room [number=").append(number)
            .append(", occupant=").append(occupant).append("]");
        return builder.toString();
    }
}
