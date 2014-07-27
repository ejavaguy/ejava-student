package ejava.examples.orm.rel.composite;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DoorPK implements Serializable {
	private static final long serialVersionUID = -5621257030133324800L;
	@Column(name="PK_HOUSE_ID") //overridden
	private int houseId;
	@Column(name="PK_DOOR_ID") //overridden
	private int doorId;
	
	public DoorPK() {}
	public DoorPK(int houseId, int doorId) {
		this.houseId=houseId;
		this.doorId=doorId;
	}
	
	public int getHouseId() { return houseId; }
	public int getDoorId() { return doorId; }
	
	@Override
	public int hashCode() {
		return houseId + doorId;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (obj==null) { return false; }
			if (this==obj) { return true; }
			DoorPK rhs = (DoorPK)obj;
			return houseId==rhs.houseId && doorId==rhs.doorId;
		} catch (Exception ex) { return false; }
	}
}
