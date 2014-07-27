package ejava.examples.orm.rel.composite;

import javax.persistence.*;

/**
 * This class provides an example use of a composite @IdClass primary key
 * where one of the properties of the IdClass is re-used as a foreign key.
 */
@Entity @Table(name="ORMREL_ROOM")
@IdClass(RoomPK.class)
@AttributeOverrides({
	@AttributeOverride(name = "houseId", column=@Column(name="HOUSE_ID")),
	@AttributeOverride(name = "roomId", column=@Column(name="ROOM_ID"))
})
public class Room {
	@Id 
	private int houseId;
	@Id
	private int roomId;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	//assign join column to primary key value and turn off inserts/updates here
	@JoinColumn(name="HOUSE_ID", insertable=false, updatable=false)
	private House house;
	
	public Room() {}
	public Room(House house, int roomId) {
		this.houseId=house.getId();
		this.house=house;
		this.roomId=roomId;
	}
	
	public House getHouse() { return house; }
	public int getRoomId() { return roomId; }	
}
