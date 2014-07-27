package ejava.examples.orm.rel.composite;

import java.io.Serializable;

import javax.persistence.Column;

public class RoomPK implements Serializable {
	private static final long serialVersionUID = 9037415256912960434L;
	@Column(name="PK_HOUSE_ID") //overridden
	private int houseId;
	@Column(name="PK_ROOM_ID") //overridden
	private int roomId;
	
	public RoomPK() {}
	public RoomPK(int houseId, int roomId) {
		this.houseId=houseId;
		this.roomId=roomId;
	}
	
	public int getHouseId() { return houseId; }
	public int getRoomId() { return roomId; }
	
	@Override
	public int hashCode() {
		return houseId + roomId;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (obj==null) { return false; }
			if (this==obj) { return true; }
			RoomPK rhs = (RoomPK)obj;
			return houseId==rhs.houseId && roomId==rhs.roomId;
		} catch (Exception ex) { return false; }
	}	
}
