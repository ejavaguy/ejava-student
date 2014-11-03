package info.ejava.examples.ejb.ejbjpa.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FloorDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int level;
    private List<RoomDTO> rooms;
    
    public FloorDTO() {}
    public FloorDTO(int level) {
        this.level = level;
    }
    
    
    public int getLevel() { return level; }
    public void setLevel(int level) {
        this.level = level;
    }
    
    public List<RoomDTO> getRooms() { return rooms; }
    public void setRooms(List<RoomDTO> rooms) {
        this.rooms = rooms;
    }
    public FloorDTO withRoom(RoomDTO room) {
        if (rooms == null) {
            rooms = new LinkedList<RoomDTO>();
        }
        if (room != null) {
            rooms.add(room);
        }
        return this;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FloorDTO [level=").append(level)
                .append(", rooms={");
        if (rooms!=null) { for (RoomDTO room: rooms) {
                builder.append(room).append("\n");
        }}
        builder.append("}]");
        return builder.toString();
    }
    
    
    
}
