package info.ejava.examples.ejb.ejbjpa.dto;

import java.io.Serializable;

public class RoomDTO implements Serializable {
    private int number;
    private boolean occupied;
    
    public RoomDTO() {}
    public RoomDTO(int number) {
        this.number = number;
    }
    
    public int getNumber() { return number; }
    public void setNumber(int number) {
        this.number = number;
    }
    
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoomDTO [number=").append(number)
                .append(", occupied=").append(occupied)
                .append("]");
        return builder.toString();
    }
}
