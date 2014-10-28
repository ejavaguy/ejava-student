package info.ejava.examples.ejb.ejbjpa.bo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="EJBJPA_FLOOR")
@NamedQueries({
    @NamedQuery(name="Floor.getFloors", 
            query="select f from Floor f order by level"),
    @NamedQuery(name="Floor.fetchFloor", 
            query="select f from Floor f "
                    + "join fetch f.rooms r "
                    + "join fetch r.occupant "
                    + "where f.level=:level")
})
public class Floor implements Serializable {
    @Id
    @Column(name="LEVEL", nullable=false)
    int level;
    
    @OneToMany(mappedBy="floor", 
            fetch=FetchType.LAZY,
            cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.DETACH},
            orphanRemoval=true)
    @OrderBy("number")
    List<Room> rooms;
    
    protected Floor() {}
    public Floor(int level) {
        this.level = level;
    }
        
    public int getLevel() { return level; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
    public Floor withRoom(Room room) {
        if (rooms==null) {
            rooms = new LinkedList<Room>();
        }
        if (room!=null) {
            rooms.add(room);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Floor [level=").append(level)
            .append(", rooms={").append("\n");
        if (rooms!=null) { for (Room room: rooms) {
            builder.append(room).append("\n");
        }}
        builder.append("}");
        return builder.toString();
    }
}
